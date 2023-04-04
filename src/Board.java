import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Cấu trúc dữ liệu để quản lý tương tác với bảng
 */
public class Board {
	public static final int BOARD_SIZE = 8;
	private int[][] nextTiles = new int[BOARD_SIZE][2];
	private JButton[][] tiles = new JButton[BOARD_SIZE][BOARD_SIZE];
	private int visitedTileCounter;
	private Knight knight;
	/**
	 * Xây dựng một bảng trống với ô trắng và đen.
	 */
	public Board() {
		TileHandler tileHandler = new TileHandler();

		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int column = 0; column < BOARD_SIZE; column++) {
				tiles[row][column] = new JButton();
				if ((row + column) % 2 == 0) {
					tiles[row][column].setBackground(Color.white);
				} else {
					tiles[row][column].setBackground(Color.black);
				}
				// Hiển thị màu sắc trên MACOS
				tiles[row][column].setOpaque(true);
				tiles[row][column].setBorder(new LineBorder(Color.black));
				tiles[row][column].addActionListener(tileHandler);
			}
		}

		// Khởi tạo đề xuất đến trạng thái trống an toàn
		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			nextTiles[moveNumber][0] = -1;
			nextTiles[moveNumber][1] = -1;
		}
		// Load dữ liệu cũ lên bảng
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
			Connection con = DriverManager.getConnection(url, "sa", "123");
			String sql = "SELECT * FROM DataGame";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				tiles[rs.getInt(2)][rs.getInt(3)].doClick();
			}
			con.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Đặt lại bảng về trạng thái trống.
	 *
	 * Đầu tiên, khởi tạo lại tất cả các ô theo màu ban đầu của chúng (đen và trắng).
	 * Thứ hai, loại bỏ biểu tượng của quân mã khỏi bảng. 
	 * Thứ ba, đặt lại bộ đếm cho các ô đã truy cập.
	 * Cuối cùng, đặt đề xuất cho các bước di chuyển tiếp theo sang trạng thái trống an toàn.
	 */
	public void resetBoard() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int column = 0; column < BOARD_SIZE; column++) {
				tiles[row][column].setText("");
				if ((row + column) % 2 == 0) {
					tiles[row][column].setBackground(Color.white);
				} else {
					tiles[row][column].setBackground(Color.black);
				}
			}
		}

		tiles[knight.getCurrentRow()][knight.getCurrentColumn()].setIcon(null);

		visitedTileCounter = 0;

		// Khởi tạo đề xuất đến trạng thái trống an toàn
		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			nextTiles[moveNumber][0] = -1;
			nextTiles[moveNumber][1] = -1;
		}
	}

	/**
	 * Lấy gạch ở một tọa độ được chỉ định.
	 *
	 * @param chỉ số hàng nơi đặt ô
	 * @param chỉ số hàng nơi đặt ô
	 * @return ô trên bảng
	 */
	public JButton getTile(int row, int column) {
		if (isWithinBound(row, column))
			return tiles[row][column];
		else
			return null;
	}

	/**
	 * Kiểm tra xem một ô nằm bên trong bảng.
	 *
	 * @param chỉ số hàng nơi đặt ô
	 * @param chỉ số cột nơi đặt ô
	 * @return {@code true} nếu ô nằm bên trong bảng. Nếu không {@code false}.
	 */
	public boolean isWithinBound(int row, int column) {
		return (row >= 0 && column >= 0) && (row < BOARD_SIZE && column < BOARD_SIZE);
	}

	/**
	 * Đánh dấu ô đã truy ô đã truy cập rồi
	 *
	 * @param chỉ số hàng nơi đặt ô
	 * @param chỉ số cột nơi đặt ô
	 */
	private void markAsVisited(int row, int column) {
		if (isWithinBound(row, column) && isNotVisited(row, column)) {
			visitedTileCounter++;
			tiles[row][column].setIcon(knight.getIcon());
			tiles[row][column].setBackground(Color.orange);
			tiles[row][column].setText("" + visitedTileCounter);
		}
	}

	/**
	 * Kiểm tra xem một ô đã được truy cập chưa.
	 *
	 * @param chỉ số hàng nơi đặt ô
	 * @param chỉ số cột nơi đặt ô
	 * @return {@code true} Nếu ô được truy cập. Nếu không, {@code false}
	 */
	public boolean isNotVisited(int row, int column) {
		return !(tiles[row][column].getBackground() == Color.orange);
	}

	/**
	 * Sơn ô có thể tiếp cận để gợi ý các bước di chuyển tiếp theo cho quân mã.
	 */
	private void displayMoveSuggestion() {
		int validMoveCounter = 0;

		nextTiles = knight.nextDestinations();
		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			int nextRow = nextTiles[moveNumber][0];
			int nextColumn = nextTiles[moveNumber][1];
			if (isWithinBound(nextRow, nextColumn) && isNotVisited(nextRow, nextColumn)) {
				tiles[nextRow][nextColumn].setBackground(Color.green);
				validMoveCounter++;
			}
		}

		if (validMoveCounter == 0) {
			JOptionPane.showMessageDialog(null, "Out of valid moves!", "Tour Ended", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/**
	 * Sơn lại các ô đã được sơn để đề xuất di chuyển.
	 */
	private void clearMoveSuggestion() {
		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			int nextRow = nextTiles[moveNumber][0];
			int nextColumn = nextTiles[moveNumber][1];
			if (isWithinBound(nextRow, nextColumn) && tiles[nextRow][nextColumn].getBackground() == Color.green) {
				if ((nextRow + nextColumn) % 2 == 0) {
					tiles[nextRow][nextColumn].setBackground(Color.white);
				} else {
					tiles[nextRow][nextColumn].setBackground(Color.black);
				}
			}
		}
	}

	/**
	 * Tính số khả năng truy cập của một ô.
	 *
	 * @param chỉ số hàng nơi đặt ô
	 * @param chỉ số cột nơi đặt ô
	 * @return số lượng ô mà quân mã có thể đạt được ô được chỉ định.
	 */
	private int getAccessibilityScore(int row, int column) {
		int accessibilityScore = 0;

		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			int neighborRow = row + Knight.vertical[moveNumber];
			int neighborColumn = column + Knight.horizontal[moveNumber];

			if (isWithinBound(neighborRow, neighborColumn) && isNotVisited(neighborRow, neighborColumn)) {
				accessibilityScore++;
			}
		}

		return accessibilityScore;
	}

	/**
	 * Nhận điểm khả năng truy cập tối thiểu trong một bộ sưu tập các ô được đề xuất cho các bước di chuyển tiếp theo.
	 *
	 * Đầu tiên, xác thực các điểm đến có thể truy cập tiếp theo. 
	 * Sau đó, điểm truy cập được tìm thấy cho mỗi bước di chuyển.
	 *
	 * @param nextTiles Một bộ sưu tập các bước di chuyển tiếp theo cho chuyến tham quan
	 * @return Điểm khả năng truy nhập thấp nhất trong số các ô
	 */
	private int getMinAccessibilityScore(int[][] nextTiles) {
		int minScore = Knight.MAX_MOVE_NUM;

		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			int row = nextTiles[moveNumber][0];
			int column = nextTiles[moveNumber][1];

			if (isWithinBound(row, column) && isNotVisited(row, column)) {
				minScore = Math.min(minScore, getAccessibilityScore(row, column));
			}
		}

		return minScore;
	}

	/**
	 * Nhận số bước di chuyển tối ưu từ các nước đi được đề xuất.
	 *
	 * Đầu tiên, xác thực các điểm đến có thể truy cập tiếp theo.
	 * Sau đó, điểm truy cập được tìm thấy cho mỗi bước di chuyển.
	 * Nếu chế độ tối ưu hóa đang bật, hãy quan sát những ô vuông 
	 * có thể truy cập được từ các ô vuông "đã đi" để quyết định ô tốt nhất 
	 * dựa trên điểm khả năng truy cập tối thiểu của chúng.
	 *
	 * @param nextTiles Một bộ sưu tập các bước di chuyển tiếp theo cho chuyến tham quan
	 * @param optimizedTiedSquares Bật / Tắt chế độ tối ưu hóa khi gặp phải các ô vuông "đã đi"
	 * @return Số bước di chuyển tối ưu mà một hiệp sĩ có thể thực hiện
	 */
	private int getOptimalMoveNumber(int[][] nextTiles, boolean optimizedTiedSquares) {
		int minScore = Knight.MAX_MOVE_NUM, optimalMoveNumber = -1;

		for (int moveNumber = 0; moveNumber < Knight.MAX_MOVE_NUM; moveNumber++) {
			int nextRow = nextTiles[moveNumber][0];
			int nextColumn = nextTiles[moveNumber][1];

			if (isWithinBound(nextRow, nextColumn) && isNotVisited(nextRow, nextColumn)) {
				int score = getAccessibilityScore(nextRow, nextColumn);
				if (score < minScore) {
					minScore = score;
					optimalMoveNumber = moveNumber;
				} else if (optimizedTiedSquares && score == minScore) {
					int[][] currOptimalNextTiles = knight.nextDestinations(nextTiles[optimalMoveNumber][0],
							nextTiles[optimalMoveNumber][1]);
					int[][] tiedSquareNextTiles = knight.nextDestinations(nextTiles[moveNumber][0],
							nextTiles[moveNumber][1]);

					if (getMinAccessibilityScore(tiedSquareNextTiles) < getMinAccessibilityScore(
							currOptimalNextTiles)) {
						optimalMoveNumber = moveNumber;
					}
				}
			}
		}

		return optimalMoveNumber;
	}

	/**
	 * Di chuyển hiệp sĩ dựa trên cách truy cập heuristic.
	 *
	 * Thứ nhất, các bước di chuyển tiếp theo được tạo ra.
	 * Sau đó, di chuyển tối ưu được tìm thấy. Hiệp sĩ di chuyển đến ô đó. 
	 * Nếu không tìm thấy động thái tối ưu, một thông báo lỗi sẽ được hiển thị.
	 *
	 * @param optimizedTiedSquares Bật / Tắt chế độ tối ưu hóa khi gặp phải các ô vuông "đã đi"
	 * @return {@code true} nếu thực hiện nước đi hợp lệ. Nếu không,
	 *         {@code false}.
	 */
	private boolean moveKnight(boolean optimizedTiedSquares) {
		nextTiles = knight.nextDestinations();

		int optimalMoveNumber = getOptimalMoveNumber(nextTiles, optimizedTiedSquares);

		if (optimalMoveNumber <= -1) {
			JOptionPane.showMessageDialog(null, "Out of valid moves!", "Tour Ended", JOptionPane.PLAIN_MESSAGE);
		} else {
			tiles[knight.getCurrentRow()][knight.getCurrentColumn()].setIcon(null);
			if (knight.move(optimalMoveNumber)) {
				markAsVisited(nextTiles[optimalMoveNumber][0], nextTiles[optimalMoveNumber][1]);
				return true;
			}
		}

		return false;
	}

	/**
	 * Thực hiện chuyến tham quan bằng phương pháp Heuristic
	 *
	 * @param intialRow    chỉ số hàng bắt đầu của chuyến quan
	 * @param intialColumn chỉ số cột bắt đầu của chuyến tham quan
	 * @param optimized    bật/tắt chế độ tối ưu hóa
	 */
	public void runTour(int intialRow, int intialColumn, boolean optimized) {
		knight = new Knight("knight.png", intialRow, intialColumn);
		markAsVisited(intialRow, intialColumn);
		while (visitedTileCounter < BOARD_SIZE * BOARD_SIZE && moveKnight(optimized)) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}

		if (visitedTileCounter >= BOARD_SIZE * BOARD_SIZE) {
			JOptionPane.showMessageDialog(null, "All tiles have been visited!", "Full Tour", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/**
	 * Một lớp để xử lý tương tác của người dùng với các ô trên bảng.
	 */
	private class TileHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			// Xóa mọi xuất di chuyển
			clearMoveSuggestion();

			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int column = 0; column < BOARD_SIZE; column++) {
					// Cố gắng di chuyển hiệp sĩ đến ô đã nhấn và kiểm tra xem ô có nhấn hay chưa
					if (source == tiles[row][column] && isNotVisited(row, column)) {
						// Khởi tạo hiệp sĩ trong nước đi đầu tiên
						if (visitedTileCounter == 0) {
							knight = new Knight("knight.png", row, column);
//                            System.out.println(knight.getCurrentRow() + "-" + knight.getCurrentColumn());
							// Đánh dấu ô đã di chuyển đến
							markAsVisited(row, column);
							// Lưu bước di chuyển vào cơ cở dữ liệu
							try {
								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
								Connection con = DriverManager.getConnection(url, "sa", "123");
								String sql = "INSERT INTO DataGame VALUES(?, ?)";
								PreparedStatement st = con.prepareStatement(sql);
								st.setInt(1, knight.getCurrentRow());
								st.setInt(2, knight.getCurrentColumn());
								st.execute();
								con.close();
							} catch (Exception ex) {
								System.out.println(ex);
							}
							// Xóa bước di chuyển trùng trong cơ sở dữ liệu
					        try {
					            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					            String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
					            Connection con = DriverManager.getConnection(url, "sa", "123");
					            String sql = "WITH cte AS (SELECT RowGame, ColumnGame, ROW_NUMBER() OVER (PARTITION BY RowGame, ColumnGame ORDER BY RowGame, ColumnGame ) row_num FROM DataGame ) DELETE FROM cte WHERE row_num > 1;";
					            Statement st = con.createStatement();
					            st.execute(sql);
					            con.close();
					        } catch (Exception ex) {
					            System.out.println(ex);
					        }
						} else {
							// Xóa ảnh tại vị trí mà hiệp sĩ đã di chuyển trước đó
							tiles[knight.getCurrentRow()][knight.getCurrentColumn()].setIcon(null);
							//Di chuyển hiệp sĩ đến vị trí mới
							if (knight.move(row, column)) {
								// Đánh dấu ô hiệp sĩ đã di chuyển đến
								markAsVisited(row, column);
								// Lưu bước di chuyển vào cơ cở dữ liệu
								try {
									Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
									String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
									Connection con = DriverManager.getConnection(url, "sa", "123");
									String sql = "INSERT INTO DataGame VALUES(?, ?)";
									PreparedStatement st = con.prepareStatement(sql);
									st.setInt(1, knight.getCurrentRow());
									st.setInt(2, knight.getCurrentColumn());
									st.execute();
									con.close();
								} catch (Exception ex) {
									System.out.println(ex);
								}
								// Xóa bước di chuyển trùng trong cơ sở dữ liệu
						        try {
						            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						            String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
						            Connection con = DriverManager.getConnection(url, "sa", "123");
						            String sql = "WITH cte AS (SELECT RowGame, ColumnGame, ROW_NUMBER() OVER (PARTITION BY RowGame, ColumnGame ORDER BY RowGame, ColumnGame ) row_num FROM DataGame ) DELETE FROM cte WHERE row_num > 1;";
						            Statement st = con.createStatement();
						            st.execute(sql);
						            con.close();
						        } catch (Exception ex) {
						            System.out.println(ex);
						        }
							}
							// Nếu thất bại, đặt ảnh hiệp sĩ trở lại
							else {
								tiles[knight.getCurrentRow()][knight.getCurrentColumn()].setIcon(knight.getIcon());
							}
						}
					}
				}
			}

			// Kiểm tra xem tất cả các ô đã được truy cập chưa
			if (visitedTileCounter >= BOARD_SIZE * BOARD_SIZE) {
				JOptionPane.showMessageDialog(null, "All tiles have been visited!", "Full Tour",
						JOptionPane.PLAIN_MESSAGE);
			} else {
				// Đề xuất bước di chuyển
				displayMoveSuggestion();
			}
		}
	}
	public void undo() {
		// Xóa bước di chuyển cuối cùng đã lưu trong cơ sở dữ liệu
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
			Connection con = DriverManager.getConnection(url, "sa", "123");
			String sql = "DELETE FROM DataGame WHERE ID=(SELECT MAX(id) FROM DataGame)";
			Statement st = con.createStatement();
			st.execute(sql);
			con.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		// Khởi tạo bảng
		resetBoard();
		// Load các di chuyển
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;database=KnightTour;trustServerCertificate=true;";
			Connection con = DriverManager.getConnection(url, "sa", "123");
			String sql = "SELECT * FROM DataGame";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				tiles[rs.getInt(2)][rs.getInt(3)].doClick();
			}
			con.close();
		} catch (Exception e) {
		}
	}

}
