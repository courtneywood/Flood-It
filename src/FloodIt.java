import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PFont;

public class FloodIt extends PApplet {
	PFont f;
	Cell[][] board = new Cell[25][25];
	Cell[][] compboard = new Cell[25][25];
	ArrayList<Cell> flood = new ArrayList<>();
	ArrayList<Cell> compflood = new ArrayList<>();
	Random r = new Random();
	boolean gameover;
	public void draw() {
		if (!gameover) {
			cells();
		}
	}
	
	public void setup() {
		f = createFont("Courier", 45, true);
		//fill cells with random colors
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 25; j++) {
				int randColor = r.nextInt(6);
				Color col = new Color(randColor);
				Cell ce = new Cell(i, j, col);
				Cell ce2 = new Cell(i, j, col);
				board[i][j] = ce;
				compboard[i][j] = ce2;
			}
		}
		noStroke();
		flood.add(board[0][0]);
		compflood.add(compboard[0][0]);
		fill(255, 255, 255);
		rect(0, 0, 1210, 50);
		fill(0, 0, 0);
		textFont(f, 40);
		text("You", 250, 35);
		text("Computer", 810, 35);
		rect(600, 0, 10, 650);
	}
	
	public void cells() {
		//draw player's cells
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 25; j++) {
				//get cell
				Cell ce = board[i][j];
				Color c = ce.getColor();
				//fill
				fill(c.r, c.g, c.b);
				rect(24*i, 24*j + 50, 24, 24);
			}
		}
		
		//draw computer's cells
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 25; j++) {
				//get cell
				Cell ce = compboard[i][j];
				Color c = ce.getColor();
				//fill
				fill(c.r, c.g, c.b);
				rect(24*i + 610, 24*j + 50, 24, 24);
			}
		}
	}
	
	public void makeMove(Cell clicked) {
		//get the color of clicked cell
		Color newColor = clicked.getColor();
		//add new edges to flood to the flood
		boolean done = false;
		while (!done) {
			ArrayList<Cell> edgesToFlood = getMatchingEdges(newColor, flood, board);
			if (edgesToFlood.size() == 0) done = true;
			else flood.addAll(edgesToFlood);
		}
		
		//update the color of the new flood cells
		for (int i = 0; i < flood.size(); i++) {
			Cell curr = flood.get(i);
			int cx = curr.x;
			int cy = curr.y;
			board[cx][cy].setColor(newColor);
		}
	}
	
	public boolean won(Cell[][] board) {
		//returns true if the board is all the same color
		Color winningColor = board[0][0].getColor();
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 25; j++) {
				if (!board[i][j].getColor().equals(winningColor)) return false;
			}
		}
		return true;
	}
	
	public ArrayList<Cell> getMatchingEdges(Color c, ArrayList<Cell> flood, Cell[][] board) {
		//returns an arraylist of all cells bordering the flooded area with color c
		ArrayList<Cell> matchingEdges = new ArrayList<>();
		//iterate through all flooded cells
		//if there's a neighbor with the matching color that's not already added, add it
		for (int i = 0; i < flood.size(); i++) {
			Cell curr = flood.get(i);
			//get neighbors of curr
			ArrayList<Cell> neighbors = getNeighbors(curr, board);
			for (int j = 0; j < neighbors.size(); j++) {
				Cell currNeighbor = neighbors.get(j);
				if (currNeighbor.getColor().equals(c)) {
					int neighborx = currNeighbor.x;
					int neighbory = currNeighbor.y;
					//if the cell is not part of the flood or already added to matchingEdges, add to matchingEdges
					if (!listContainsCell(flood, neighborx, neighbory) && !listContainsCell(matchingEdges, neighborx, neighbory)) {
						matchingEdges.add(board[neighborx][neighbory]);
					}		
				}
			}
		}
		return matchingEdges;
	}
	
	public boolean listContainsCell(ArrayList<Cell> list, int x, int y) {
		//returns true if list contains a cell with x and y
		for (int i = 0; i < list.size(); i++) {
			Cell curr = list.get(i);
			if (curr.x == x && curr.y == y) return true;
		}
		return false;
	}
	
	public ArrayList<Cell> getNeighbors(Cell c, Cell[][] board) {
		ArrayList<Cell> neighbors = new ArrayList<>();
		int x = c.x;
		int y = c.y;
		if (x != 0) {
			neighbors.add(board[x - 1][y]);
		}
		if (x != board.length - 1) {
			neighbors.add(board[x + 1][y]);
		}
		if (y != 0) {
			neighbors.add(board[x][y - 1]);
		}
		if (y != board.length - 1) {
			neighbors.add(board[x][y + 1]);
		}
		return neighbors;
	}
	
	public void compMakeMove() {
		//pick the color that the most neighbors are
		//iterate through all colors
		Color[] colors = new Color[6];
		Color currColor = compflood.get(0).getColor();
		//initialize colors
		for (int i = 0; i < 6; i++) {
			Color newColor = new Color(i);
			colors[i] = newColor;
		}
		//find the color that the highest number of neighbors have
		Color bestColor = colors[0];
		ArrayList<Cell> newCells = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			if (!colors[i].equals(currColor)) {
				boolean done = false;
				ArrayList<Cell> tempcompflood = new ArrayList<>();
				tempcompflood.addAll(compflood);
				while (!done) {
					ArrayList<Cell> edgesToFlood = getMatchingEdges(colors[i], tempcompflood, compboard);
					if (edgesToFlood.size() == 0) done = true;
					else tempcompflood.addAll(edgesToFlood);
				}
				//if this color has the greatest neighbors so far
				int newCellsNum = tempcompflood.size() - compflood.size();
				if (newCellsNum > newCells.size()) {
					bestColor = colors[i];
					tempcompflood.removeAll(compflood);
					newCells = tempcompflood;
				}
			}
		}
		
		compflood.addAll(newCells);
		
		for (int i = 0; i < compflood.size(); i++) {
			Cell curr = compflood.get(i);
			int cx = curr.x;
			int cy = curr.y;
			compboard[cx][cy].setColor(bestColor);
		}
	}
	
	public void mousePressed() {
		//get color of square clicked
		//if the square clicked is not the first square
		if (!(mouseX > 0 && mouseX < 24 && mouseY > 0 && mouseY < 24)) {
			//get the x and y of the square clicked
			//if they clicked inside their board
			if (mouseX < 600 && mouseY > 50) {
				Cell clickedCell = findCellClicked(mouseX, mouseY);
				makeMove(clickedCell);
				//computer makes move immediately after player
				compMakeMove();
			}
			if (won(board)) {
				draw();
				fill(0,0,0);
				textFont(f, 72);
				text("You won!", 450, 360);
				gameover = true;
			}
			else if (won(compboard)) {
				draw();
				fill(0,0,0);
				textFont(f, 72);
				text("Computer won!", 380, 360);
				gameover = true;
			}
		}
	}
	
	public Cell findCellClicked(int mouseX, int mouseY) {
		mouseY = mouseY - 50;
		int x = 0;
		int y = 0;
		x = mouseX / 24;
		y = mouseY / 24;
		if (x == board.length) x--;
		if (y == board.length) y--;
		return board[x][y];
	}
	
	public void settings() {
		size(1210, 650);
	}
	
	public static void main(String[] args) {
		PApplet.main("FloodIt");
	}
	
	
	class Cell {
		int x;
		int y;
		Color c;
		public Cell(int x, int y, Color c) {
			this.x = x;
			this.y = y;
			this.c = c;
		}
		
		public Color getColor() {
			return c;
		}
		
		public void setColor(Color newColor) {
			c = newColor;
		}
	}
	
	class Color {
		int r;
		int g;
		int b;
		public Color(int color) {
			switch(color) {
			case 0:
				r = 255;
				b = 0;
				g = 0;
				break;
			case 1:
				r = 255;
				b = 0;
				g = 255;
				break;
			case 2:
				r = 0;
				g = 255;
				b = 0;
				break;
			case 3:
				r = 0;
				g = 0;
				b = 255;
				break;
			case 4:
				r = 204;
				g = 0;
				b = 255;
				break;
			case 5:
				r = 0;
				g = 255;
				b = 255;
				break;
			}
		}
		
		boolean equals(Color otherColor) {
			if (r == otherColor.r && b == otherColor.b && g == otherColor.g) return true;
			else return false;
		}
	}
}
