package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Unit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 06/01/15
 */
public class AIGameStateDebugger extends JFrame {

	static final boolean enabled = true;
	static final boolean hold = true;
	static final boolean single = false;

	static List<WeakReference<AIGameState>> gameStates = new ArrayList<WeakReference<AIGameState>>();
	static List<AIGameState> onHoldStates = new ArrayList<AIGameState>();
	static int                              activeState = 0;

	static AIGameStateDebugger instance = null;
	private static AIGameStateDebuggerPanel pane;

	public static void debug(AIGameState gameState) {
		if (enabled) {
			if (hold) {
				if (single && onHoldStates.size() > 0) {
					onHoldStates.set(0, gameState);
				} else {
					onHoldStates.add(gameState);
				}
			}
			for (int i = 0; i < gameStates.size(); i++) {
				if (gameStates.get(i)
							  .get() == null) {

					gameStates.remove(i);
				}
			}
			gameStates.add(new WeakReference<AIGameState>(gameState));

			if (instance == null) {
				instance = new AIGameStateDebugger();
			}
			instance.setVisible(true);
			pane.recompute();
			instance.repaint();
		}
	}

	public AIGameStateDebugger() {
		setSize(new Dimension(800, 500));
		Dimension dim = Toolkit.getDefaultToolkit()
							   .getScreenSize();
		setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		pane = new AIGameStateDebuggerPanel();
		setContentPane(pane);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	class AIGameStateDebuggerPanel
			extends JPanel {

		private final JLabel player1Info;
		private final JLabel player2Info;
		private final JLabel totalProbs1;
		private final JLabel totalProbs2;
		private final JLabel left1;
		private final JLabel left2;

		public AIGameStateDebuggerPanel() {
			final AIGameStateDebuggerPanel self = this;
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			JPanel grid = new JPanel(new GridLayout(10, 10));
			AIUnitListener listener = new AIUnitListener();
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 10; x++) {
					JButton cell = new GridCell("" + x + ";" + y);
					cell.addMouseListener(listener);
					grid.add(cell);
				}
			}
			add(grid);
			JPanel bar = new JPanel();
			final JLabel count = new JLabel("0");
			count.setPreferredSize(new Dimension(50, 20));
			final JLabel pos = new JLabel("0");
			pos.setPreferredSize(new Dimension(50, 20));
			JButton left = new JButton("<-");
			left.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (activeState > 0) {
						activeState--;
						pos.setText("" + (activeState + 1));
						count.setText("" + gameStates.size());
						recompute();
						self.repaint();
					}
				}
			});
			JButton right = new JButton("->");
			right.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (activeState < gameStates.size() - 1) {
						activeState++;
						pos.setText("" + (activeState + 1));
						count.setText("" + gameStates.size());
						recompute();
						self.repaint();
					}
				}
			});
			bar.add(left);
			bar.add(pos);
			bar.add(right);
			bar.add(count);
			add(bar);
			JPanel stateInfo = new JPanel();
			JScrollPane stateScroller = new JScrollPane(stateInfo);
			stateInfo.setLayout(new BoxLayout(stateInfo, BoxLayout.Y_AXIS));
			player1Info = new JLabel("player1Info");
			totalProbs1 = new JLabel("totalProbs1");
			left1 = new JLabel("left1");
			player2Info = new JLabel("player2Info");
			totalProbs2 = new JLabel("totalProbs2");
			left2 = new JLabel("left2");
			stateInfo.add(player1Info);
			stateInfo.add(totalProbs1);
			stateInfo.add(left1);
			stateInfo.add(player2Info);
			stateInfo.add(totalProbs2);
			stateInfo.add(left2);
			add(stateScroller);
			recompute();
		}

		public void recompute() {
			AIGameState state = gameStates.get(activeState)
										  .get();
			if (state != null) {
				player1Info.setText(state.getPlayerInformation(PLAYER_1)
										 .toString());
				player2Info.setText(state.getPlayerInformation(PLAYER_2).toString());
				String probString1 = "";
				String probString2 = "";
				String l1 = "";
				String l2 = "";
				for (int i = 3; i < Unit.UnitType.values().length; i++) {
					Unit.UnitType type = Unit.UnitType.values()[i];
					float[] totalProbabilityFor = state.getTotalProbabilityFor(type);
					probString1 += type.toString()+": "+ Math.round(totalProbabilityFor[0])+" ";
					probString2 += type.toString()+": "+ Math.round(totalProbabilityFor[1])+" ";
					AIGameState.PlayerInformation information1 = state.getPlayerInformation(PLAYER_1);
					AIGameState.PlayerInformation information2 = state.getPlayerInformation(PLAYER_2);
					l1 += type.toString()+": "+ Math.round(type.getQuantity()- information1.getDefeatedFor(type)) + " ";
					l2 += type.toString()+": "+ Math.round(type.getQuantity()- information2.getDefeatedFor(type)) + " ";
				}
				totalProbs1.setText(probString1);
				totalProbs2.setText(probString2);
				left1.setText(l1);
				left2.setText(l2);
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return getParent().getPreferredSize();
		}
	}

	static class GridCell
			extends JButton {

		public GridCell(String text) {
			super(text);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			WeakReference<AIGameState> reference = gameStates.get(activeState);
			AIGameState state = reference.get();
			if (state != null) {
				String label = getText();
				String[] split = label.split(";");
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				AIUnit unit = state.getAIUnit(x, y);
				Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());

				if (unit.getOwner() == NEMO) {
					Unit refUnit = unit.getUnitReference();
					if (refUnit.isAir()) {
						g2.setColor(Color.lightGray);
					} else if (refUnit.isLake()) {
						g2.setColor(Color.darkGray);
					} else {
						g2.setColor(Color.white);
					}
				} else if (unit.getOwner() == PLAYER_1) {
					g2.setColor(Color.blue.brighter());
				} else {
					g2.setColor(Color.red.brighter());
				}
				if (state.isOverloaded(x, y)) {
					float halfWidth = (float) getWidth() / 2f;
					Rectangle2D.Double rect1 = new Rectangle2D.Double(0, 0, halfWidth, getHeight());
					Rectangle2D.Double rect2 = new Rectangle2D.Double(halfWidth, 0, halfWidth, getHeight());
					g2.setColor(Color.blue.brighter());
					g2.fill(rect1);
					g2.setColor(Color.red.brighter());
					g2.fill(rect2);

				} else {
					g2.fill(rect);
				}
				g2.setColor(Color.black);
				g2.draw(rect);
			}
		}
	}

	static class AIUnitListener extends MouseAdapter {

		static JFrame frame;

		public AIUnitListener() {
			frame = new JFrame("AIUnit");
			frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
			frame.setSize(new Dimension(200, 400));
			JPanel pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
			frame.setContentPane(pane);
		}



		@Override
		public void mouseEntered(MouseEvent e) {
			JPanel panel = (JPanel)frame.getContentPane();
			panel.removeAll();
			if (activeState < gameStates.size()) {
				WeakReference<AIGameState> reference = gameStates.get(activeState);
				AIGameState state = reference.get();
				if (state != null) {
					JButton source = (JButton) e.getSource();
					String label = source.getText();
					String[] split = label.split(";");
					int x = Integer.parseInt(split[0]);
					int y = Integer.parseInt(split[1]);
					AIUnit unit = state.getAIUnit(x, y);
					panel.add(new JLabel(unit.getOwner()
											 .toString()));
					panel.add(new JLabel(""+unit.getUnitReference()));
					panel.add(new JLabel("Moved: " + unit.isMoved()));
					panel.add(new JLabel("Revealed: " + unit.isRevealed()));
					for (int i = 3; i < Unit.UnitType.values().length; i++) {
						Unit.UnitType type = Unit.UnitType.values()[i];
						panel.add(new JLabel(type.toString()+": "+unit.getProbabilityFor(type)));
					}
					if (state.isOverloaded(x, y)) {
						panel.add(new JLabel("Overload"));
						AIUnit unit2 = state.getAIUnitFor(x, y, PLAYER_2);
						panel.add(new JLabel(unit2.getOwner()
												 .toString()));
						panel.add(new JLabel("" + unit2.getUnitReference()));
						panel.add(new JLabel("Moved: " + unit2.isMoved()));
						panel.add(new JLabel("Revealed: " + unit2.isRevealed()));
						for (int i = 3; i < Unit.UnitType.values().length; i++) {
							Unit.UnitType type = Unit.UnitType.values()[i];
							panel.add(new JLabel(type.toString() + ": " + unit2.getProbabilityFor(type)));
						}
					}
					panel.repaint();
					frame.setVisible(true);
				}
			}
		}
	}

}
