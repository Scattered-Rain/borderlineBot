package borderlineBot.util.hashing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import borderlineBot.Launcher;
import borderlineBot.util.hashing.Hasher.Hash;

/** Static class Managing Hashed Maps and their Board Info */
public class HashManager{
	
	/** Stores information of hashed maps */
	private HashMap<Hash, BoardInfo> memory;
	
	
	/** Constructs new HashManager */
	public HashManager(int ... filesOfTableHashFilesIndex){
		this.memory = new HashMap<Hash, BoardInfo>();
		for(int c=0; c<filesOfTableHashFilesIndex.length; c++){
			int index = filesOfTableHashFilesIndex[c];
			if(index>=0 && index<TABLE_HASH_FILES.length){
				loadTable(TABLE_HASH_FILES[index]);
			}
		}
	}
	
	
	/** Returns HashMemory of given hash value */
	public BoardInfo get(Hash hash){
		return memory.get(hash);
	}
	
	/** Returns whether the given Hash exists in the Memory */
	public boolean has(Hash hash){
		return memory.containsKey(hash);
	}
	
	/** Puts the given board info into the Hash Map with the given Hash as key */
	public void put(Hash hash, BoardInfo boardInfo){
		boardInfo.hash = hash;
		if(!Launcher.COMPETITIVE){
			if(has(hash)){
				BoardInfo bInfo = get(hash);
				//Check for error with hashCode value
				if(!bInfo.hash.equals(hash)){
					System.out.println("Equal HashCode for different Boards!");
					System.exit(0);
				}
				//Counter for ubiquitusness of given Board
				else{
					boardInfo.seen = bInfo.seen+1;
				}
			}
			else{
				boardInfo.seen = 1;
			}
		}
		memory.put(hash, boardInfo);
	}
	
	/** Loads in TableFile values */
	private void loadTable(String file){
		try{
			FileReader fReader = new FileReader(FILE_PATH+file+".txt");
			BufferedReader reader = new BufferedReader(fReader);
			Scanner scanner = new Scanner(reader);
			while(scanner.hasNextLine() && scanner.hasNext()){
				//long, long
				Hash hash = new Hash(scanner.nextLong(), scanner.nextLong());
				//Int
				int seen = scanner.nextInt();
				//Float
				float score = scanner.nextFloat();
				//inject values into BoardInfo, put BoardInfo into HashMap
				BoardInfo readInfo = new BoardInfo();
				readInfo.seen = seen;
				readInfo.setScore(score);
				put(hash, readInfo);
			}
			fReader.close();
			reader.close();
			scanner.close();
			System.out.println("Loading TableHash Successful");
		}catch(Exception ex){
			System.out.println("Couldn't load TableHash!");
			if(!Launcher.COMPETITIVE){
				System.exit(0);
			}
		};
	}
	
	
	//--statics--
	/** The name of the table file */
	private static final String FILE_PATH = "tableHash"+File.separatorChar;
	
	/** Array containing the names of the TableHash Files that can be loaded for the HashManager */
	public static final String[] TABLE_HASH_FILES = new String[]{"basic"};
	
	/** Static reference to the managers */
	private static HashManager manager = new HashManager();
	
	
	/** Initializes Static HashManager */
	public static void initHashManager(int ... fileOfTableHashFilesIndex){
		manager = new HashManager(fileOfTableHashFilesIndex);
	}
	
	
	/** Statically returns board info of given Hash */
	public static BoardInfo sGet(Hash hash){
		return manager.get(hash);
	}
	
	/** Statically returns whether the given Hash is in memory */
	public static boolean sHas(Hash hash){
		return manager.has(hash);
	}
	
	/** Statically puts given Board into memory with given Hash */
	public static void sPut(Hash hash, BoardInfo boardInfo){
		manager.put(hash, boardInfo);
	}
	
	/** Writes the HashMap of the Manager onto a File */
	public static void writeTableManager(String file){
		writeTable(manager.memory, file);
	}
	
	/** Writes the table contents of the given HashMap to the Table File, DO NOT CALL THIS DURING A GAME! */
	public static void writeTable(HashMap<Hash, BoardInfo> map, String file){
		Iterator<BoardInfo> boards = map.values().iterator();
		try{
			BoardInfo current;
			PrintWriter writer = new PrintWriter(FILE_PATH+file+".txt", "UTF-8");
			while(boards.hasNext()){
				current = boards.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append(current.getHash().getPrimaryLong()+" "+current.getHash().getSecondaryLong()+" ");
				buffer.append(current.getSeen()+" ");
				buffer.append(current.getScore()+" ");
				writer.println(buffer.toString());
			}
			writer.close();
			System.out.println("Writing TableHash Successful");
		}catch(Exception ex){
			System.out.println("Couldn't write TableHash!");
			if(!Launcher.COMPETITIVE){
				System.exit(0);
			}
		}
	}
	
	
	//--classes--
	/** Container storing information about a GameBoard, used for storing in the HashMap */
	public static class BoardInfo{
		
		/** Actual Hash Value of this Map (Auto added when put into HashManager) */
		@Getter private Hash hash;
		
		/** How often this board has been seen (Auto added when put into HashManager when not Competitive) */
		@Getter private int seen;
		
		/** The score of the given map */
		@Getter @Setter private float score;
		
	}
	
}
