import javax.swing.*;

/**
 * Cấu trúc dữ liệu để điều khiển chuyển động của một quân mã trên bàn cờ.
 */
public class Knight {
    public static final int MAX_MOVE_NUM = 8;
    public static final int[] horizontal  = new int[]{ 2, 1, -1, -2, -2, -1, 1, 2 };
    public static final int[] vertical    = new int[]{ -1, -2, -2, -1, 1, 2, 2, 1 };
    private ImageIcon icon;
    private int currentRow;
    private int currentColumn;

    /**
     * Khởi tạo một quân mã được sử dụng trên {@link Board}.
     *
     * @param imageFile tên tệp của hình ảnh biểu tượng hiệp sỹ
     * @param initialRow hàng nơi hiệp sĩ bắt đầu chuyến tham quan
     * @param initialColumn cột nơi hiệp sĩ bắt đầu chuyến tham quan
     */
    public Knight(String imageFile, int initialRow, int initialColumn) {
        setIcon(imageFile);
        this.currentRow = initialRow;
        this.currentColumn = initialColumn;
    }

    /**
     * Nhận hình ảnh biểu tượng của quân mã.
     *
     * @return hình ảnh biểu tượng của quân mã
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Đặt hình ảnh biểu tượng của quân mã được hiển thị trên một {@link Board}.
     *
     * @param imageFile tên tệp của hình ảnh biểu tượng quân mã
     */
    public void setIcon(String imageFile) {
        this.icon = new ImageIcon(imageFile);
    }

    /**
     * Kiểm tra xem quân mã có thể đến được ô đích bằng chuyển động hình chữ L của nó hay không.
     *
     * @param nextRow chỉ số của hàng mà quân mã sẽ được di chuyển đến
     * @param nextColumn chỉ mục của cột mà quân mã sẽ được di chuyển đến
     * @return {@code true} nếu nước đi có hình dạng hợp lệ. Ngược lại, {@code false}
     */
    public boolean isValidMoveShape(int nextRow, int nextColumn) {
        int rowDiff = Math.abs(nextRow - currentRow);
        int columnDiff = Math.abs(nextColumn - currentColumn);

        return (rowDiff == 1 && columnDiff == 2) || (rowDiff == 2 && columnDiff == 1);
    }

    /**
     * Tạo các ô có thể đi tiếp theo mà quân mã có di chuyển.
     *
     * Tọa độ của chúng được tính toán dựa trên vị trí hiện tại của quân mã đi qua.
     * Do đó, {@link Board} cần xác thực chúng trước khi sử dụng.
     *
     * @return một loạt các ô có thể tiếp cận được
     */
    public int[][] nextDestinations() {
        int[][] nextTiles = new int[MAX_MOVE_NUM][2];

        for (int moveNumber = 0; moveNumber < MAX_MOVE_NUM; moveNumber++) {
            nextTiles[moveNumber][0] = currentRow + vertical[moveNumber];
            nextTiles[moveNumber][1] = currentColumn + horizontal[moveNumber];
        }

        return nextTiles;
    }

    /**
     * Tạo các ô có thể tiếp theo mà quân mã có thể di chuyển.
     *
     * Tọa độ của chúng được tính toán dựa trên hàng và cột được chỉ định.
     * Tuy nhiên, các ô được trả lại có thể nằm ngoài bàn cờ hoặc đã được quân mã đi qua.
     * Do đó, {@link Board} cần xác thực chúng trước khi sử dụng.
     *
     * @param row the row from which the result is calculated
     * @param column the column from which the result is calculated
     * @return một loạt các ô có thể tiếp cận được
     */
    public int[][] nextDestinations(int row, int column) {
        int[][] nextTiles = new int[MAX_MOVE_NUM][2];

        for (int moveNumber = 0; moveNumber < MAX_MOVE_NUM; moveNumber++) {
            nextTiles[moveNumber][0] = row + vertical[moveNumber];
            nextTiles[moveNumber][1] = column + horizontal[moveNumber];
        }

        return nextTiles;
    }

    /**
     * Di chuyển quân mã đến ô tiếp theo dựa trên số lần di chuyển.
     *
     * @param moveNumber số nằm giữa 0 đến 7
     */
    public boolean move(int moveNumber) {
        if (moveNumber < MAX_MOVE_NUM) {
            currentRow += vertical[moveNumber];
            currentColumn += horizontal[moveNumber];
            return true;
        }
        else
            return false;
    }

    /**
     * Di chuyển quân mã đến một ô được chỉ định.
     *
     * @param nextRow hàng mà quân mã di chuyển đến
     * @param nextColumn cột mà quân mã di chuyển đến
     * @return {@code true} nếu di chuyển có thể được thực hiện. Nếu không, {@code false}.
     */
    public boolean move(int nextRow, int nextColumn) {
        if (isValidMoveShape(nextRow, nextColumn)) {
            currentRow = nextRow;
            currentColumn = nextColumn;
            return true;
        }

        return false;
    }

    /**
     * Nhận hàng nơi quân mã hiện đang ở.
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Lấy cột nơi quân mã hiện đang ở.
     */
    public int getCurrentColumn() {
        return currentColumn;
    }

}
