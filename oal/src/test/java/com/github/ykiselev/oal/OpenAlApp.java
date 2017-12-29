package com.github.ykiselev.oal;

import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.io.ByteChannelAsMemoryUtilByteBuffer;
import org.junit.jupiter.api.Assertions;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.util.List;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_info;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_open_memory;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_stream_length_in_samples;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OpenAlApp {

    public static void main(String[] args) {
        new OpenAlApp().run();
    }

    private void run() {
        final long device = alcOpenDevice((CharSequence) null);
        if (device == NULL) {
            throw new IllegalArgumentException("No device found!");
        }

        try {
            final ALCCapabilities capabilities = createCapabilities(device);
            if (!capabilities.OpenALC10) {
                throw new IllegalArgumentException("OpenAL 1.0 is not supported!");
            }
            System.out.println("OpenALC10: " + capabilities.OpenALC10);
            System.out.println("OpenALC11: " + capabilities.OpenALC11);
            System.out.println("caps.ALC_EXT_EFX = " + capabilities.ALC_EXT_EFX);


            if (capabilities.OpenALC11) {
                List<String> devices = getStringList(NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER);
                if (devices == null) {
                    // todo checkALCError(NULL);
                } else {
                    for (int i = 0; i < devices.size(); i++) {
                        System.out.println(i + ": " + devices.get(i));
                    }
                }
            }

            String defaultDeviceSpecifier = alcGetString(NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
            Assertions.assertTrue(defaultDeviceSpecifier != null);
            System.out.println("Default device: " + defaultDeviceSpecifier);

            final long context = alcCreateContext(device, (IntBuffer) null);
            EXTThreadLocalContext.alcSetThreadContext(context);
            AL.createCapabilities(capabilities);

            System.out.println("ALC_FREQUENCY: " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
            System.out.println("ALC_REFRESH: " + alcGetInteger(device, ALC_REFRESH) + "Hz");
            System.out.println("ALC_SYNC: " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
            System.out.println("ALC_MONO_SOURCES: " + alcGetInteger(device, ALC_MONO_SOURCES));
            System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(device, ALC_STEREO_SOURCES));

            try {
                testPlayback();
            } finally {
                alcMakeContextCurrent(NULL);
                alcDestroyContext(context);
            }
        } finally {
            alcCloseDevice(device);
        }
    }

    private void checkALError() {
        final int err = alGetError();
        if (err != AL_NO_ERROR) {
            throw new RuntimeException(alGetString(err));
        }
    }

    private void testPlayback() {
        // generate buffers and sources
        final int buffer = alGenBuffers();
        checkALError();

        final int source = alGenSources();
        checkALError();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            final ShortBuffer pcm = readVorbis("/footsteps.ogg", 32 * 1024, info);

            //copy to buffer
            alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
            checkALError();
        }

        //set up source input
        alSourcei(source, AL_BUFFER, buffer);
        checkALError();

        //lets loop the sound
        alSourcei(source, AL_LOOPING, AL_FALSE);
        checkALError();

        //play source 0
        alSourcePlay(source);
        checkALError();

        // not sure this is correct method to wait for sound to stop
        int state;
        while ((state = alGetSourcei(source, AL_SOURCE_STATE)) != AL_STOPPED){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        //stop source 0
        alSourceStop(source);
        checkALError();

        //delete buffers and sources
        alDeleteSources(source);
        checkALError();

        alDeleteBuffers(buffer);
        checkALError();
    }

    private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
        try (Wrap<ByteBuffer> vorbis = new ByteChannelAsMemoryUtilByteBuffer(
                Channels.newChannel(getClass().getResourceAsStream(resource)),
                bufferSize
        ).read()) {
            IntBuffer error = BufferUtils.createIntBuffer(1);
            long decoder = stb_vorbis_open_memory(vorbis.value(), error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        }
    }
}
