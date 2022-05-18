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

package be.panako.strategy.sql.storage;

//import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.logging.Logger;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
// import com.sun.org.apache.bcel.internal.generic.NEW;

import be.panako.cli.Panako;
import be.panako.strategy.sql.SQLFingerprint;
import be.panako.util.Config;
import be.panako.util.FileUtils;
import be.panako.util.Key;
import be.panako.util.StopWatch;
import java.sql.*;

public class SQLDBStorage implements Storage {
	private final static Logger LOG = Logger.getLogger(SQLDBStorage.class.getName());

	/**
	 * The single instance of the storage.
	 */
	private static Storage instance;

	/**
	 * A mutex for synchronization purposes
	 */
	private static Object mutex = new Object();

	/**
	 * @return Returns or creates a storage instance. This should be a thread safe
	 *         operation.
	 */
	public synchronized static Storage getInstance() {
		if (instance == null) {
			synchronized (mutex) {
				if (instance == null) {
					instance = new SQLDBStorage();
				}
			}
		}
		return instance;
	}

	// private BasicDBObject audioNameStore=null;

	private DBCollection fftFingerprintStore;

	private static final String SECONDS_COUNTER = "seconds_counter";

	MongoClient mongo;
	MongoCredential credential;
	MongoDatabase database;
	DBCollection audioNameStore;
	DBCollection counter;

	// private final ConcurrentNavigableMap<Integer, String> audioName=null;

	public SQLDBStorage() {

		// Creating a Mongo client
		mongo = new MongoClient(Config.get(Key.NFFT_MONGODB_HOST), Integer.parseInt(Config.get(Key.NFFT_MONGODB_PORT)));

		// Creating Credentials

		// credential = MongoCredential.createCredential("sampleUser", "myDb",
		// "password".toCharArray());
		// mongo System.out.println("Connected to the database successfully");

		final String audioStore = "audio_store";
		final String fftStore = "fft_store";

		// Accessing the database
		database = mongo.getDatabase("mcsk");
		DB db = mongo.getDB("mcsk");

//	       database.createCollection(audioStore, null);
//	       
//	       database.createCollection(fftStore, null);

		database.listCollectionNames();
		audioNameStore = db.getCollection(audioStore);

		audioNameStore = db.getCollection(audioStore);

		fftFingerprintStore = db.getCollection(fftStore);

		counter = db.getCollection(SECONDS_COUNTER);

		// If the current application writes to the storage of if a new database has to
		// be created
		// create the stores.
		if (Panako.getCurrentApplication() == null || (Panako.getCurrentApplication().writesToStorage())) {
			// Check for and create a lock.

		} else if (Panako.getCurrentApplication().needsStorage()) {

		} else {
			// no database is needed
			audioNameStore = null;
			fftFingerprintStore = null;
			database = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#addAudio(int, java.lang.String)
	 */
	@Override
	public void addAudio(int identifier, String description) {
		// audioName.put(identifier, description);

		BasicDBObject document = new BasicDBObject();
		document.put("identifier", identifier);
		document.put("description", description);
		audioNameStore.insert(document);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#audioObjectAdded(int)
	 */
	@Override
	public void audioObjectAdded(int numberOfSeconds) {
//		db.atomicLong(SECONDS_COUNTER).open().addAndGet(numberOfSeconds);

		DBObject dbRes = counter.findOne();

		if (dbRes.containsField(SECONDS_COUNTER)) {

			System.out.println(dbRes.get(SECONDS_COUNTER).toString());
			double val = (Double) dbRes.get(SECONDS_COUNTER);

			val += numberOfSeconds;
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put(SECONDS_COUNTER, val);
			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", newDocument);
			counter.update(dbRes, updateObject);

		} else {
			BasicDBObject document = new BasicDBObject();
			document.put(SECONDS_COUNTER, numberOfSeconds);
			counter.insert(document);
		}

//		db.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#getNumberOfFingerprints()
	 */
	@Override
	public int getNumberOfFingerprints() {
		return (int) fftFingerprintStore.count();
	}

	public void checkNumberOfFingerprints() {

		Iterator<DBObject> it = fftFingerprintStore.find();
		int counter = 0;
		while (it.hasNext()) {
			counter++;
			it.next();
		}
		assert counter == getNumberOfFingerprints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#getAudioDescription(int)
	 */
	@Override
	public String getAudioDescription(int identifier) {
		String description = null;
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("identifier", identifier);
		DBCursor cursor = audioNameStore.find(searchQuery);
		while (cursor.hasNext()) {
			description = cursor.next().get("description").toString();
		}
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#getNumberOfAudioObjects()
	 */
	@Override
	public int getNumberOfAudioObjects() {
		return (int) audioNameStore.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#getNumberOfSeconds()
	 */
	@Override
	public double getNumberOfSeconds() {
		double val;
		DBObject dbRes = counter.findOne();
		if (dbRes.containsField(SECONDS_COUNTER)) {
			val = (double) dbRes.get(SECONDS_COUNTER);
		} else {
			val = 0.0;
		}
		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#hasDescription(java.lang.String)
	 */
	@Override
	public boolean hasDescription(String description) {
		int indentifier = FileUtils.getIdentifier(description);
		return description.equals(getAudioDescription(indentifier));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#addFingerprint(int, int, int)
	 */
	@Override
	public float addFingerprint(int identifier, int time, int landmarkHash) {
		int[] value = { landmarkHash, time, identifier };

		if (fftFingerprintStore.count() == 0) {
			BasicDBObject document = new BasicDBObject("$addToSet",
					new BasicDBObject("value", new BasicDBObject("$each", value)));

			System.out.println(document.toString());
			fftFingerprintStore.update(new BasicDBObject("_id", new ObjectId("1")), document);

		} else {

			BasicDBObject document = new BasicDBObject("$addToSet",
					new BasicDBObject("value", new BasicDBObject("$each", value)));
			System.out.println(document.toString());
			fftFingerprintStore.update(new BasicDBObject("_id", new ObjectId("1")), document);

		}

		// document.put("fingerprint", value);
		// fftFingerprintStore.insert(document);
		return 0.0f;
	}

	@Override
	public float addFingerprint(HashSet<int[]> arrayFIngerprints) {

		BasicDBObject document = new BasicDBObject("$addToSet",
				new BasicDBObject("value", new BasicDBObject("$each", arrayFIngerprints)));

		BasicDBObject update = new BasicDBObject("_id", "501c2824484ff81318b5643a");

		try {
			FileOutputStream outputStream = new FileOutputStream("/opt/panako/dbs/finger.txt");
			byte[] strToBytes = document.toString().getBytes();
			outputStream.write(strToBytes);

			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fftFingerprintStore.update(update, document);
		return 0.0f;
	}

	public <T extends Object> void checkType(T object) {
		if (object instanceof Integer)
			System.out.println("Integer ");
		else if (object instanceof Double)
			System.out.println("Double ");
		else if (object instanceof Float)
			System.out.println("Float : ");
		else if (object instanceof List)
			System.out.println("List! ");
		else if (object instanceof Set)
			System.out.println("Set! ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.panako.strategy.nfft.storage.Storage#getMatches(java.util.List, int)
	 */
	@Override
	public List<SQLFingerprintQueryMatch> getMatches(List<SQLFingerprint> fingerprints, int size) {

		StopWatch w = new StopWatch();
		Set<SQLFingerprintHit> allHits = new HashSet<SQLFingerprintHit>();
		for (SQLFingerprint fingerprint : fingerprints) {
			int hash = fingerprint.hash();
			int[] fromElement = { hash, Integer.MIN_VALUE, Integer.MIN_VALUE };
			int[] toElement = { hash, Integer.MAX_VALUE, Integer.MAX_VALUE };

			DBCursor cursor = fftFingerprintStore.find();
			BasicDBList list = (BasicDBList) cursor.next().get("value");
			NavigableSet<int[]> nfttFingerprint = new TreeSet<>();

			Iterator<Object> iterator = list.iterator();
//
//			while (iterator.hasNext()) {
//
//				BasicDBList arr = (BasicDBList) iterator.next();
//				System.out.println(iterator.next());
//
//				int[] finger =  { Integer.parseInt(arr.get(0).toString()), Integer.parseInt(arr.get(1).toString()),
//						Integer.parseInt(arr.get(2).toString()) };
//
//				System.out.println(finger);
//
//				try {
//					// Trying to access element at index 8
//					// which will throw an Exception
//					nfttFingerprint.add(finger);
//
//				} catch (Exception e) {
//					System.out.println("add");
//					e.printStackTrace();
//					System.out.println(e);
//					System.exit(1);
//				}
//
//			}
//
//			System.out.println(nfttFingerprint);
//			try {
//				System.out.println(nfttFingerprint.subSet(fromElement, toElement));
//
//			} catch (Exception e) {
//				System.out.println("subset");
//				e.printStackTrace();
//				
//			}
			
			

			//Iterator<int[]> it = nfttFingerprint.subSet(fromElement, toElement).iterator();
			while (iterator.hasNext()) {
				//int[] hit = it.next();
				SQLFingerprintHit lh = new SQLFingerprintHit();
				int queryTime = fingerprint.t1;// queryTimeForHash.get(landmarkHash);
			
			
				
				try {
					BasicDBList ls=(BasicDBList) iterator.next();
					System.out.println(ls);
					lh.matchTime = (int) ls.get(1);
					lh.identifier = (int) ls.get(2);
	
				} catch (Exception e) {
					System.out.println("loop");
					System.out.println(e);
					e.printStackTrace();
					continue;
					
				}
				
				
				
				
				lh.timeDifference = lh.matchTime - queryTime;
				lh.queryTime = queryTime;
				allHits.add(lh);
			}
		}
		LOG.info(String.format("MapDB answerd to query of %d hashes in %s and found %d hits.", fingerprints.size(),
				w.formattedToString(), allHits.size()));

		HashMap<Integer, List<SQLFingerprintHit>> hitsPerIdentifer = new HashMap<Integer, List<SQLFingerprintHit>>();
		for (SQLFingerprintHit hit : allHits) {
			if (!hitsPerIdentifer.containsKey(hit.identifier)) {
				hitsPerIdentifer.put(hit.identifier, new ArrayList<SQLFingerprintHit>());
			}
			List<SQLFingerprintHit> hitsForIdentifier = hitsPerIdentifer.get(hit.identifier);
			hitsForIdentifier.add(hit);
		}

		// This could be done in an SQL where clause also (with e.g. a group by
		// identifier /having count(identifier) >= 5 clause)
		// removes random chance hash hits.
		int minMatchingLandmarksThreshold = 3;
		for (Integer identifier : new HashSet<Integer>(hitsPerIdentifer.keySet())) {
			if (hitsPerIdentifer.get(identifier).size() < minMatchingLandmarksThreshold) {
				hitsPerIdentifer.remove(identifier);
			}
		}

		// Holds the maximum number of aligned offsets per identifier
		// The key is the number of aligned offsets. The list contains a list of
		// identifiers.
		// The list will most of the time only contain one entry.
		// The most common offset will be at the top of the list (reversed integer
		// order).
		TreeMap<Integer, List<Integer>> scorePerIdentifier = new TreeMap<Integer, List<Integer>>(reverseIntegerOrder);
		// A map that contains the most popular offset per identifier
		HashMap<Integer, Integer> offsetPerIdentifier = new HashMap<Integer, Integer>();

		// iterate every list per identifier and count the most popular offsets
		for (Integer identifier : hitsPerIdentifer.keySet()) {
			// use this hash table to count the most popular offsets
			HashMap<Integer, Integer> popularOffsetsPerIdentifier = new HashMap<Integer, Integer>();
			// the final score for the identifier
			int maxAlignedOffsets = 0;

			// add the offsets for each landmark hit
			for (SQLFingerprintHit hit : hitsPerIdentifer.get(identifier)) {
				if (!popularOffsetsPerIdentifier.containsKey(hit.timeDifference)) {
					popularOffsetsPerIdentifier.put(hit.timeDifference, 0);
				}
				int numberOfAlignedOffsets = 1 + popularOffsetsPerIdentifier.get(hit.timeDifference);
				popularOffsetsPerIdentifier.put(hit.timeDifference, numberOfAlignedOffsets);
				if (numberOfAlignedOffsets > maxAlignedOffsets) {
					maxAlignedOffsets = numberOfAlignedOffsets;
					offsetPerIdentifier.put(identifier, hit.timeDifference);
				}
			}
			// Threshold on aligned offsets. Ignores identifiers with less than 3 aligned
			// offsets
			if (maxAlignedOffsets > 4) {
				if (!scorePerIdentifier.containsKey(maxAlignedOffsets)) {
					scorePerIdentifier.put(maxAlignedOffsets, new ArrayList<Integer>());
				}
				scorePerIdentifier.get(maxAlignedOffsets).add(identifier);
			}
		}

		// Holds the maximum number of aligned and ordered offsets per identifier
		// The key is the number of aligned offsets. The list contains a list of
		// identifiers.
		// The list will most of the time only contain one entry.
		// The most common offset will be at the top of the list (reversed integer
		// order).
		TreeMap<Integer, List<Integer>> scoreOderedPerIdentifier = new TreeMap<Integer, List<Integer>>(
				reverseIntegerOrder);

		// check if the order in the query is the same as the order in the reference
		// audio
		for (Integer alignedOffsets : scorePerIdentifier.keySet()) {
			List<Integer> identifiers = scorePerIdentifier.get(alignedOffsets);
			for (Integer identifier : identifiers) {
				// by making it a set only unique times are left
				HashMap<Integer, SQLFingerprintHit> hitsWithBestOffset = new HashMap<Integer, SQLFingerprintHit>();
				for (SQLFingerprintHit hit : hitsPerIdentifer.get(identifier)) {
					if (hit.timeDifference == offsetPerIdentifier.get(identifier)) {
						hitsWithBestOffset.put(hit.queryTime, hit);
					}
				}
				List<SQLFingerprintHit> hitsToSortyByQueryTime = new ArrayList<SQLFingerprintHit>(
						hitsWithBestOffset.values());
				List<SQLFingerprintHit> hitsToSortyByReferenceTime = new ArrayList<SQLFingerprintHit>(
						hitsWithBestOffset.values());
				Collections.sort(hitsToSortyByQueryTime, new Comparator<SQLFingerprintHit>() {
					@Override
					public int compare(SQLFingerprintHit o1, SQLFingerprintHit o2) {
						return Integer.valueOf(o1.queryTime).compareTo(o2.queryTime);
					}
				});
				Collections.sort(hitsToSortyByReferenceTime, new Comparator<SQLFingerprintHit>() {
					@Override
					public int compare(SQLFingerprintHit o1, SQLFingerprintHit o2) {
						return Integer.valueOf(o1.matchTime).compareTo(o2.matchTime);
					}
				});

				int countInOrderAlignedHits = 0;
				for (int i = 0; i < hitsToSortyByQueryTime.size(); i++) {
					if (hitsToSortyByQueryTime.get(i).equals(hitsToSortyByReferenceTime.get(i))) {
						countInOrderAlignedHits++;
					}
				}
				if (countInOrderAlignedHits > 4) {
					if (!scoreOderedPerIdentifier.containsKey(countInOrderAlignedHits)) {
						scoreOderedPerIdentifier.put(countInOrderAlignedHits, new ArrayList<Integer>());
					}
					scoreOderedPerIdentifier.get(countInOrderAlignedHits).add(identifier);
				}
			}
		}

		List<SQLFingerprintQueryMatch> matches = new ArrayList<SQLFingerprintQueryMatch>();
		for (Integer alignedOffsets : scoreOderedPerIdentifier.keySet()) {
			List<Integer> identifiers = scoreOderedPerIdentifier.get(alignedOffsets);
			for (Integer identifier : identifiers) {
				SQLFingerprintQueryMatch match = new SQLFingerprintQueryMatch();
				match.identifier = identifier;
				match.score = alignedOffsets;
				match.mostPopularOffset = offsetPerIdentifier.get(identifier);
				if (matches.size() < size) {
					matches.add(match);
				}
			}
			if (matches.size() >= size) {
				break;
			}
		}
		return matches;
	}

	private Comparator<Integer> reverseIntegerOrder = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}
	};

}

//class Finger implements Comparable< Finger>{
//    
//    private int hash;
//    private int identifier;
//    private int time;
//    
//   
//    
//   
//    
// 
//    /*
//     * Step 2. Define the compareTo method.
//     * This compareTo method will sort Emp objects
//     * in ascending order according the id
//     */
//    public int compareTo(Finger otherEmp) {
//        return getId() - otherEmp.getId();
//    }

