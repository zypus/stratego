package com.theBombSquad.stratego.gameMechanics.board;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;

import lombok.Getter;

import com.theBombSquad.stratego.gameMechanics.GameView;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class Setup extends GameBoard implements Serializable {
	@Getter
	private GameView view;

	protected Setup() {
		super();
	}
	protected Setup(GameView view) {
		super();
		this.view=view;
	}

	public Setup(int width, int height, Rectangle... lakes) {
		super(width, height, lakes);
	}

	public static void writeToFile(File file, Setup setup) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			String data = setup.compactSerialisation();
			fileWriter.write(data);
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static Setup readFromFile(File file, List<Unit> units) {
		Setup setup = null;
		try {
			Scanner scanner = new Scanner(file);
			String data = scanner.next();
			String[] split = data.split(",");
			setup = new Setup(10, 4);
			int counter = 0;
			for (int x = 0; x < setup.getWidth(); x++) {
				for (int y = 0; y < setup.getHeight(); y++) {
					Unit unit = null;
					for (int u = 0; u < units.size(); u++) {
						if (units.get(u).getType().ordinal() == Integer.parseInt(split[counter])) {
							unit = units.remove(u);
							break;
						}
					}
					setup.setUnit(x,y, unit);
					counter++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setup;
	}

	public String compactSerialisation() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Unit unit = getUnit(x,y);
				stringBuilder.append(unit.getType().ordinal());
				stringBuilder.append(",");
			}
		}
		return stringBuilder.toString();
	}

}
