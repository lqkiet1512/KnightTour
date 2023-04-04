import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.*;

/**
 * Knight's Tour được triển khai cho sự tương tác của người dùng.
 */
public class Main extends JFrame {
	int row = 0;
	int column = 0;
	boolean check = false;
	public Main() {
		// Đặt tiêu đề của chương trình
		super("Knight's Tour");
		Container contents = getContentPane();
		contents.setLayout(new GridLayout(8, 8));
		// Add Menu Bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		// Add Menu Option
		JMenu game = new JMenu("Game");
		JButton undo = new JButton("Undo");
		JButton auto = new JButton("Auto");
		menuBar.add(game);
		menuBar.add(undo);
		menuBar.add(auto);
		// Thêm tùy chọn Khởi động lại
		JMenuItem restart = new JMenuItem("New Game");
		game.add(restart);
		restart.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
					Connection con = DriverManager.getConnection(url, "sa", "123");
					String sql = "DELETE FROM DataGame";
					Statement st = con.createStatement();
					st.execute(sql);
					con.close();
				} catch (Exception ex) {
					System.out.println(ex);
				}
				dispose();
				new Main();
			}
		}));
		Board chessboard = new Board();
		undo.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				chessboard.undo();
			}
		}));
		auto.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
					Connection con = DriverManager.getConnection(url, "sa", "123");
					String sql = "SELECT * FROM DataGame WHERE ID=(SELECT MAX(id) FROM DataGame)";
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery(sql);
					while (rs.next()) {
						row = rs.getInt(2);
						column = rs.getInt(3);
						check = true;
					}
					con.close();
				} catch (Exception ex) {
					System.out.println(ex);
				}
				if(check == true) {
					chessboard.runTour(row, column, true);
				}
				else {
					chessboard.runTour(row, column, true);
				}
				
			}
		}));

		for (int row = 0; row < Board.BOARD_SIZE; row++) {
			for (int column = 0; column < Board.BOARD_SIZE; column++) {
				contents.add(chessboard.getTile(row, column));
			}
		}

		setSize(600, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void getMove() {
		
	}
	public static void main(String[] args) {
		new Main();
	}
}