package net.ottomated.OGNes;

import javax.sound.sampled.*;
import java.util.Arrays;

class AudioOut {
    public static final int BUFFER = 1024;
    private byte[] buf;
    private int cursor;
    SourceDataLine line;

    AudioOut() throws LineUnavailableException {
        buf = new byte[BUFFER];
        cursor = 0;
        AudioFormat format = new AudioFormat((float) Nes.SAMPLE_RATE, 8, 2, true, true);
        line = AudioSystem.getSourceDataLine(format);
        line.open(format, BUFFER * 2);
        line.start();
    }

    void play(float left, float right) {
        //int outsample = (short) (right * Short.MAX_VALUE);
        buf[cursor] = (byte) (right * Byte.MAX_VALUE);
        buf[cursor+ 1] = (byte) (left * Byte.MAX_VALUE);
        cursor+=2;
        if (cursor >= BUFFER - 1) {
            cursor = 0;
            //System.out.println(Arrays.toString(getByteData(buf)));
            line.write(buf, 0, BUFFER);
        }
    }

    private static byte[] getByteData(float[] samples) {
        byte[] pcm = new byte[AudioOut.BUFFER * 2];
        int sampleIndex = 0,
                pcmIndex = 0;

        while (sampleIndex < AudioOut.BUFFER) {
            short outsample = (short) (samples[sampleIndex] * Short.MAX_VALUE);
            pcm[pcmIndex] = (byte) (outsample & 0xff);
            pcm[pcmIndex + 1] = (byte) ((outsample >> 8) & 0xff);

            sampleIndex++;
            pcmIndex += 2;
        }

        return pcm;
    }
}
