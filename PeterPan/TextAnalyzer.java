package PeterPan;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import PeterPan.Trie;

public class TextAnalyzer {
	private String text;
	private List<String> chapters;
	private String delimiter = "[,.\";?!-():\\[\\]]"; 
	
	private class Word {
		private String text;
		private int wordCount;
		public Word(String text, int wordCount) {
			this.text = text;
			this.wordCount = wordCount;
		}
		
		public int getWordCount() {
			return this.wordCount;
		}
		
		public String getText() {
			return this.text;
		}
 	}
	
	public TextAnalyzer(File file) {
		try {
			this.text = new String(Files.readAllBytes(file.toPath()));			
			this.chapters = Arrays.asList(this.text.split("Chapter [0-9]+ .*")).subList(1, this.text.split("Chapter [0-9]+ .*").length);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getChapters() {
		return this.chapters;
	}
	
	public void readFileAndParseChapters(File file) throws IOException {		
		this.text = new String(Files.readAllBytes(file.toPath()));		
	}
	
	public String getText() {
		return this.text;
	}
	
	public int getTotalNumberOfWords(File file) {
		int wordCount = 0;
		try {		
			Scanner input = new Scanner(file);
			while(input.hasNextLine()) {						
				wordCount += processString(input.nextLine()).length;
			}	
			input.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}		
		return wordCount;					
	}
	
	public int getTotalUniqueWords(File file) {
		HashSet<String> uniqueWords = new HashSet<>();
		try {
			Scanner input = new Scanner(file);
			while(input.hasNextLine()) {							
				String[] words = processString(input.nextLine());
				for(String word : words) {						
					uniqueWords.add(word.toLowerCase());					
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uniqueWords.size();	
	}
	
	// boolean maxQueue is used to specify the sorting. True for max heap. False for min heap.
	public PriorityQueue<Word> createWordFrequncyQueue(File file, boolean maxQueue) {
		HashMap<String, Integer> individualWordCount = new HashMap<>();		
		PriorityQueue<Word> queue = 
				maxQueue ? new PriorityQueue<Word>((word1, word2) -> word2.getWordCount() - word1.getWordCount())
						 : new PriorityQueue<Word>((word1, word2) -> word1.getWordCount() - word2.getWordCount());					
		try {
			Scanner input = new Scanner(file);
			while(input.hasNextLine()) {
				String[] words = processString(input.nextLine());
				for(String word : words) {
					individualWordCount.put(word, individualWordCount.getOrDefault(word, 0) + 1);				
				}							
			}
			for(String key : individualWordCount.keySet()) {
				Word word = new Word(key, individualWordCount.get(key));
				queue.offer(word);
			}							
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queue;		
	}
	
	public List<String[]> get20MostFrequentWords(File file) {		
		PriorityQueue<Word> queue = this.createWordFrequncyQueue(file, true);
		ArrayList<String[]> results = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			Word word = queue.poll();
			results.add(new String[] {word.getText(), String.valueOf(word.getWordCount())});						
		}
		return results;
	}
	
	public List<String[]> get20MostInterestingFrequentWords(File file) {
		PriorityQueue<Word> queue = this.createWordFrequncyQueue(file, true);
		HashSet<String> commonWords = new HashSet<>();
		String[] words = new String[] {
			"the", "of", "to", "and", "a", "in", "is", "it", "you", "that", 
			"he", "was", "for", "on", "are", "with", "as", "I", "his", "they", 
			"be", "at", "one", "have", "this", "from", "or", "had", "by", 
			"not", "word", "but", "what", "some", "we", "can", "out", 
			"other", "were", "all", "there", "when", "up", "use", "your", 
			"how", "said", "an", "each", "she", "which", "do", "their", "time", 
			"if", "will", "way", "about", "many", "then", "them", "write", "would", 
			"like", "so", "these", "her", "long", "make", "thing", "see", "him", "two", 
			"has", "look", "more", "day", "could", "go", "come", "did", "number", "sound", 
			"no", "most", "people", "my", "over", "know", "water", "than", "call", "first", 
			"who", "may", "down", "side", "been", "now", "ind"
		};
		for(String word : words) {
			commonWords.add(word);
		}
		ArrayList<String[]> results = new ArrayList<>();		
		while(results.size() < 20) {
			Word word = queue.poll();
			if(!commonWords.contains(word.getText().toLowerCase())) {
				results.add(new String[] {word.getText(), String.valueOf(word.getWordCount())});								
			}								
		}
		return results;
	}
	
	public List<String[]> get20LeastFrequentWords(File file) {
		PriorityQueue<Word> queue = this.createWordFrequncyQueue(file, false);
		ArrayList<String[]> results = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			Word word = queue.poll();
			results.add(new String[] {word.getText(), String.valueOf(word.getWordCount())});						
		}
		return results;
	}
		
	public List<Integer> getFrequencyOfWord(String word) {
		ArrayList<Integer> frequencyOfWord = new ArrayList<>();		
		for(String chapter : this.getChapters()) {
			String[] words = processChapter(chapter);
			int count = 0;
			for(String wd : words) {
				if(wd.equals(word)) {
					count++;
				}
			}
			frequencyOfWord.add(count);	
		}			
		return frequencyOfWord;
	}
	
	public int getChapterQuoteAppears(String quote) {
		for(int i = 0; i < chapters.size(); i++) {
			if(chapters.get(i).contains(quote)) {
				return i + 1;
			}
		}
		return -1;
	}
	
	public String generateSentence() {
		StringBuilder sb = new StringBuilder("The ");
		ArrayList<String> wordsAfterThe = new ArrayList<>();
		for(String chapter : this.getChapters()) {
			String[] words = processChapter(chapter);
			for(int j = 0; j < words.length - 1; j++) {
				if(words[j].equals("the") || words[j].equals("The")) {
					wordsAfterThe.add(words[j + 1]);
				}
			}
		}		
		for(int i = 0; i < 20; i++) {
			sb.append(wordsAfterThe.get((int)(Math.random() * wordsAfterThe.size())));
			sb.append(i == 19 ? "." : " ");
		}
		return sb.toString();		
	}
	
	public List<String> getAutocompleteSentence(String startOfSentence) {		
		Trie trie = new Trie();
		for(String chapter : this.getChapters()) {
			String[] words = processChapter(chapter);
			for(String word : words) {
				trie.insert(word);
			}
		}	
		return trie.getSuggestedList(startOfSentence); 
	}
	
	public String[] processChapter(String chapter) {
		chapter = chapter.replaceAll(this.delimiter, " ");
		return chapter.split("\\s");	
	}
	
	public static String[] processString(String line) {
		line = line.replaceAll("[,.\";?!-():\\[\\]]", " ");
		ArrayList<String> filters = new ArrayList<>();
		for(String word : line.split("\\s")) {
			if(word.length() != 0) {
				filters.add(word);
			}
		}		
		return filters.toArray(new String[filters.size()]);			
	}

}
