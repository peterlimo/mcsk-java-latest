/***************************************************************************
*                                                                          *
* Panako - acoustic fingerprinting                                         *
* Copyright (C) 2014 - 2017 - Joren Six / IPEM                             *
*                                                                          *
* This program is free software: you can redistribute it and/or modify     *
* it under the terms of the GNU Affero General Public License as           *
* published by the Free Software Foundation, either version 3 of the       *
* License, or (at your option) any later version.                          *
*                                                                          *
* This program is distributed in the hope that it will be useful,          *
* but WITHOUT ANY WARRANTY; without even the implied warranty of           *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
* GNU Affero General Public License for more details.                      *
*                                                                          *
* You should have received a copy of the GNU Affero General Public License *
* along with this program.  If not, see <http://www.gnu.org/licenses/>     *
*                                                                          *
****************************************************************************
*    ______   ________   ___   __    ________   ___   ___   ______         *
*   /_____/\ /_______/\ /__/\ /__/\ /_______/\ /___/\/__/\ /_____/\        *
*   \:::_ \ \\::: _  \ \\::\_\\  \ \\::: _  \ \\::.\ \\ \ \\:::_ \ \       *
*    \:(_) \ \\::(_)  \ \\:. `-\  \ \\::(_)  \ \\:: \/_) \ \\:\ \ \ \      *
*     \: ___\/ \:: __  \ \\:. _    \ \\:: __  \ \\:. __  ( ( \:\ \ \ \     *
*      \ \ \    \:.\ \  \ \\. \`-\  \ \\:.\ \  \ \\: \ )  \ \ \:\_\ \ \    *
*       \_\/     \__\/\__\/ \__\/ \__\/ \__\/\__\/ \__\/\__\/  \_____\/    *
*                                                                          *
****************************************************************************
*                                                                          *
*                              Panako                                      *
*                       Acoustic Fingerprinting                            *
*                                                                          *
****************************************************************************/




package be.panako.util;

/**
 * Defines which values can be configured and their respective
 * default values.
 * @author Joren Six
 */
public enum Key{
	/**
	 * Checks the data store if the file # is already added. Set this to
	 * false if a large number of unique files are added for a small
	 * performance gain.
	 */
	CHECK_DUPLICATE_FILE_NAMES("TRUE"), 
	
	/**
	 * The maximum file size (in bytes) of files that are stored. Default is 60 megabytes.
	 */
	MAX_FILE_SIZE("62914560"),
	
	/**
	 * The step size while monitoring a long audio fragment, in seconds.
	 */
	MONITOR_STEP_SIZE("25"),

	/**
	 * The overlap, also in seconds. By default detection resolution is 
	 * 25-5=20 seconds.
	 */
	MONITOR_OVERLAP("5"),
	
	
	/**
	 * Enabling JLibAV allows support for almost all audio formats in the
	 * known universe. When disabled Panako supports Flac, MP3, and Vorbis,
	 * using buggy Java implementations Configuring libAV and enabling
	 * JLibAV is strongly advised (see readme.txt).
	 */
	DECODER("PIPE"),
	
	/**
	 * The pipe command environment
	 */
	DECODER_PIPE_ENVIRONMENT("/bin/bash"),
	/**
	 * The pipe command argument
	 */
	DECODER_PIPE_ENVIRONMENT_ARG("-c"),

	/**
	 * The command that streams PCM audio to a pipe
	 */
	DECODER_PIPE_COMMAND("ffmpeg -ss %input_seeking%  %number_of_seconds% -i \"%resource%\" -vn -ar %sample_rate% -ac %channels% -sample_fmt s16 -f s16le pipe:1"),

	/**
	 * The buffer used to cache the results from 
	 * the pipe. 44100 bytes is half a second.
	 */
	DECODER_PIPE_BUFFER_SIZE("44100"),
	
	/**
	 * The log file for the pipe decoder.
	 */
	DECODER_PIPE_LOG_FILE("decoder_log.txt"),
	
	
	/**
	 * The number of processors available to Panako. If zero (or less) all
	 * available processors are used.
	 */
	AVAILABLE_PROCESSORS("5"), 	
	

	
	PCH_FILES("dbs/pch"),
	PCH_SAMPLE_RATE(22050),
	PCH_OVERLAP(1024),
	PCH_SIZE(2048), 
	
	
	NFFT_MONGODB_PORT(27017),
	
	NFFT_MONGODB_HOST("127.0.0.1"),
	
	
	/**
	 * The name of the MapDB database location.
	 */
	NFFT_MAPDB_DATABASE("/opt/panako/dbs/nfft_panako_db"),
	

	/**
	 * The sample rate of the input signal
	 */
	NFFT_SAMPLE_RATE(8000),	
	/**
	 * The size of the audio block and consequently the size (in samples) of the FFT.
	 */
	NFFT_SIZE(512),
	/**
	 * The size of the audio block step size (in samples).
	 */
	NFFT_STEP_SIZE(256),
	
	/**
	 * Allows only  a limited amount of event points each frame.
	 * Especially useful for percussive onsets.
	 */
	NFFT_EVENT_POINTS_MAX_PER_FFT_FRAME(3),

	/**
	 * Minium euclidean distance between event points. 
	 * The value is expressed in milliseconds cents
	 */
	NFFT_EVENT_POINT_MIN_DISTANCE(600),
	/**
	 * The maximum number of fingerpints per event points (fan-out).
	 */
	NFFT_MAX_FINGERPRINTS_PER_EVENT_POINT(2),
	
	
	/**
	 * The size of the Min filter halve of the MIN/MAX filter for 
	 * spectral peak detection, it should be odd.
	 */
	NFFT_MIN_FILTER_WINDOW_SIZE(7),
	/**
	 * The size of the MAX filter halve of the MIN/MAX filter for 
	 * spectral peak detection, it should be odd.
	 */
	 NFFT_MAX_FILTER_WINDOW_SIZE(15),
	 
	 /**
	 * Use a hash based on refined pitch estimates using the phase
	 * or not.
	 */
	NFFT_USE_PHASE_REFINED_HASH("FALSE"), 
	 
	 
		
	/**
	 * The name of the MapDB database location.
	 */
	NCTEQ_MAPDB_DATABASE("cteq_panako_db"),
	/**
	 * The expected sample rate for the constant q transform.
	 */
	NCTEQ_SAMPLE_RATE(44100),
	/**
	 * Step size in samples for the constant q transform.
	 */
	NCTEQ_STEP_SIZE(1536),
	/**
	 * The minimum pitch, in absolute cents 3383cents is about +-77 Hz.
	 */
	NCTEQ_MIN_FREQ(3700),
	/**
	 * The maximum pitch, in absolute cents. 11533 cents is about +-6392.63 Hz.
	 */
	NCTEQ_MAX_FREQ(12200),
	/**
	 * The number of bins per octave for the constant q transform
	 */
	NCTEQ_BINS_PER_OCTAVE(36),
	/**
	 * The maximum number of event points generated for each second of analyzed
	 * audio to store in the database (Hz).
	 */
	NCTEQ_EVENT_POINTS_PER_SECOND_FOR_STORAGE(8),
	/**
	 * The number of event points per audio second for queries (Hz)
	 */
	NCTEQ_EVENT_POINTS_PER_SECOND_FOR_QUERY(8),
	/**
	 * The maximum delta between two frequency components in one 
	 * fingerprint, in cents
	 */
	NCTEQ_EVENT_POINT_FREQUENCY_DELTA_MAX(1066),
	/**
	 * Defines how much fingerprints can be connected to one 
	 * event point in the spectrum. Increasing this factor improves 
	 * retrieval rate but limits performance and storage needs.
	 */
	NCTEQ_EVENT_POINT_BRANCHING_FOR_STORAGE(1),
	/**
	 * The branching factor for a query needs to be higher to make sure
	 * matches are found, but not too high, so no unneeded hash collisions
	 * are found.
	 */
	NCTEQ_EVENT_POINT_BRANCHING_FOR_QUERY(4),
	/**
	 * The maximum number of hash collisions allowed in storage. It is a
	 * trade-off between recall and response time. More hash collisions
	 * means a larger search time, but more hits. Allowing more collisions
	 * also increases disk space.
	 */
	 NCTEQ_MAX_HASH_COLLISIONS(1000),
	 
	/**
	 * The minimum number of fingerprints that 
	 * need to match between a query and the reference audio. 
	 * The aim is to remove random fingerprint matches.
	 * If this is set too high, some real matches may be discarded. 
	 * Set it too low, and the matching algorithm will spend a lot of time 
	 * checking random matches.  
	 */
	NCTEQ_MINIMUM_MATCHES_THRESHOLD(4),
	/**
	 * The matching algorithm detects a real match if at least this number of fingerprints align. 
	 * If it is set too low, false positives may appear. If it is set too high some real matches
	 * may be ignored. 
	 */
	NCTEQ_MINIMUM_ALIGNED_MATCHES_THRESHOLD(3),
	
	
	
	/**
	 * The port to use for the HTTP server providing the JSON REST API.
	 */
	HTTP_SERVER_PORT(8080),
	
	
	/**
	 * The synchronization algorithm only considers the match as valid if this number of aligning matches are found.
	 */
	SYNC_MIN_ALIGNED_MATCHES(7), 
	/**
	 * The loudness delta in dB
	 */
	SYNC_MARKER_LOUDNESS_DELTA(17),
	
	/**
	 * Max time stamp for marker (in seconds)
	 */
	SYNC_MARKER_MAX_TIMESTAMP(3),
	
	/**
	 * Difference allowed in ms
	 */
	SYNC_MARKER_TIME_ERROR_ALLOWED(1),
	
	
	
	/**
	 * The name of the MapDB database location.
	 */
	RAFS_DATABASE("rafs_db.db"),
	/**
	 * The sample rate of the input signal
	 */
	RAFS_SAMPLE_RATE(5500),	
	/**
	 * The size of the audio block and consequently the size (in samples) of the FFT.
	 */
	RAFS_FFT_SIZE(2048),
	/**
	 * The size of the audio block step size (in samples). Every 64 (2048-1984) blocks at a sample rate of 5.5k = 11.6ms
	 */
	RAFS_FFT_STEP_SIZE(64),
	/**
	 * The hamming search radius
	 */
	RAFS_HAMMINNG_SEARCH_RADIUS(19),
	/**
	 * The number of chunks in the multi index hasher.
	 */
	RAFS_MIH_CHUNKS(5), 
	/**
	 * The number of bits stored in the the multi index hash.
	 */
	RAFS_HAMMING_SPACE_NUM_BITS(128),
	
	/**
	 * The number of least reliable bits to flip.
	 */
	RAFS_BITS_TO_FLIP(9),
	
	/**
	 * The Hamming distance threshold. If the number of erroneous bits for 256 fingerprints is below this
	 * threshold then it is accepted as a true positive.
	 */
	RAFS_HAMMING_DISTANCE_THRESHOLD(2867),
	
	/**
	 * The maximum time a query takes. By default, if not matches are found after 15 000 milliseconds
	 * an empty result is returned.
	 */
	RAFS_TIMEOUT(15000),
	
	
	/**
	 * The folder where metadata (json files) for each file is stored.
	 */
	META_DATA_DIRECTORY("/opt/panako/metadata"), 
	/**
	 * The strategy (algorithm) to use, CTEQ|FFT.
	 */
	STRATEGY("SQL"),	
	
	/**
	 * The command executed to get meta data.
	 */
	META_DATA_COMMAND("/opt/panako/extract_metadata.sh")  ;
	
	
	String defaultValue;
	private Key(String defaultValue){
		this.defaultValue = defaultValue;
	}
	private Key(int defaultValue){
		this(String.valueOf(defaultValue));
	}
	private Key(float defaultValue){
		this(String.valueOf(defaultValue));
	}
	public String getDefaultValue() {
		return defaultValue;
	}		
}
