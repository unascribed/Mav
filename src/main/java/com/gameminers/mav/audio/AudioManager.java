/*
 * This file is part of Mav.
 *
 * Mav is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Mav is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Mav. If not, see <http://www.gnu.org/licenses/>.
 */
package com.gameminers.mav.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;

import com.jsresources.AudioCapture;
import com.jsresources.AudioPlayback;
import com.jsresources.Constants;

public class AudioManager {
	private Mixer mixer;
	private AudioPlayback sink;
	private AudioCapture source;
	
	private Map<String, byte[]> clips = new HashMap<>();
	
	public void init() {
		Mixer.Info info = AudioSystem.getMixerInfo()[0];
		mixer = AudioSystem.getMixer(info); // TODO
		loadClip("notif1");
		loadClip("notif2");
		loadClip("notif3");
		loadClip("recog1");
		loadClip("fail1");
		loadClip("fail2");
		loadClip("listen1");
		sink = new AudioPlayback(Constants.FORMAT_CODE_CD, mixer, Constants.BUFFER_SIZE_MILLIS[Constants.BUFFER_SIZE_INDEX_DEFAULT]);
		source = new AudioCapture(Constants.FORMAT_CODE_SPHINX, mixer, Constants.BUFFER_SIZE_MILLIS[Constants.BUFFER_SIZE_INDEX_DEFAULT]);
		try {
			sink.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			source.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			sink.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			source.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadClip(String key) {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream("resources/audio/"+key+".wav");
			clips.put(key, IOUtils.toByteArray(in));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void playClip(String key) {
		if (!clips.containsKey(key))
			throw new RuntimeException("No clip with key '"+key+"'");
		try {
			sink.setAudioInputStream(AudioSystem.getAudioInputStream(new ByteArrayInputStream(clips.get(key))));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void play(AudioInputStream stream) {
		sink.setAudioInputStream(stream);
	}
	public AudioPlayback getSink() {
		return sink;
	}
	public AudioCapture getSource() {
		return source;
	}
	public void destroy() {
		sink.close();
		source.close();
	}
}
