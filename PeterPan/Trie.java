package PeterPan;
import java.util.ArrayList;
import java.util.List;

public class Trie {

	class TrieNode {
		private TrieNode[] children;
		private boolean isEnd;
		
		public TrieNode() {
			this.children = new TrieNode[26];
		}
	}
	
	private TrieNode root;
	public Trie() {
		this.root = new TrieNode();
	}
	
	public void insert(String word) {		
		word = word.toLowerCase();
		TrieNode cursor = this.root;
		for(int i = 0; i < word.length(); i++) {
			int index = word.charAt(i) - 'a';
			if(index < 26 && index >= 0) {
				if(cursor.children[index] == null) {
					cursor.children[index] = new TrieNode();							
				} 
				cursor = cursor.children[index];
			}		
		}
		cursor.isEnd = true;				
	}
	
	public boolean search(String word) {
		word = word.toLowerCase();
		TrieNode cursor = this.root;
		for(int i = 0; i < word.length(); i++) {
			int index = word.charAt(i) - 'a';
			if(cursor.children[index] == null) {
				return false;
			} 
			cursor = cursor.children[index];
		}
		return cursor.isEnd;
	}
	
	public List<String> getSuggestedList(String prefix) {
		ArrayList<String> results = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		prefix = prefix.toLowerCase();
		TrieNode cursor = this.root;
		for(int i = 0; i < prefix.length(); i++) {
			int index = prefix.charAt(i) - 'a';
			if(cursor.children[index] == null) {
				return results;
			}			
			cursor = cursor.children[index];			
		}
		
		findAllSuggestions(cursor, results, sb);
		
		for(int i = 0; i < results.size(); i++) {
			results.set(i, prefix + results.get(i));
		}
		return results;		
	}
	
	private void findAllSuggestions(TrieNode node, ArrayList<String> suggestions, StringBuilder sb) {
		if(node.isEnd) {			
			suggestions.add(sb.toString());			
		}
		for(int i = 0; i < node.children.length; i++) {
			if(node.children[i] != null) {				
				sb.append((char) (i + 'a'));
				findAllSuggestions(node.children[i], suggestions, sb);
				sb.setLength(sb.length() - 1);
			}
		}		
	}
		
}
