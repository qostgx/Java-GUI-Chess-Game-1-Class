import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class S25207Project02 {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new GridBagLayout());
        frame.setMaximumSize(new Dimension(1000,1000));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Board board = new Board();
        frame.add(board);
        frame.setVisible(true);
    }
}


class Board extends JPanel {

    public int titleSize = 85;
    int cols = 8;
    int rows = 8;

    ArrayList<Piece> pieceList = new ArrayList<>();

    public Piece selectedPiece;

    Input input = new Input(this);

    public int PassTile = -1;

    public Scanner scanner = new Scanner(this);

    public Board() {
        this.setPreferredSize(new Dimension(cols * titleSize, rows * titleSize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        addPieces();
    }

    public int getTileNum(int col, int row) {
        return row * rows + col;
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }


    public boolean isValidMove(Move move) {
        if (sameTeam(move.piece, move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }
        if (scanner.isKingScanned(move)) {
            return false;
        }
        return true;
    }

    public void makeMove(Move move) {
        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.name.equals("King")) {
            moveKing(move);
        }
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * titleSize;
        move.piece.yPos = move.newRow * titleSize;
        move.piece.isFirstMove = false;//For Pawn and King
        capture(move.capture);
    }


    private void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
            } else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * titleSize;
        }
    }


    public void movePawn(Move move) {
        //Pass
        int colorIndex = move.piece.isWhite ? 1 : -1;
        if (getTileNum(move.newCol, move.newRow) == PassTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
        }
        if (Math.abs(move.piece.row - move.newRow) == 2) {
            PassTile = getTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            PassTile = -1;
        }
        colorIndex = move.piece.isWhite ? 0 : 7;
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) {
        pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        pieceList.remove(piece);
    }


    public boolean sameTeam(Piece a, Piece b) {
        if (a == null || b == null) {
            return false;
        }
        return a.isWhite == b.isWhite;
    }


    Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public void addPieces() {
        pieceList.add(new Rook(this, 0, 0, false));
        pieceList.add(new Knight(this, 1, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Queen(this, 3, 0, false));
        pieceList.add(new King(this, 4, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));
        pieceList.add(new Knight(this, 6, 0, false));
        pieceList.add(new Rook(this, 7, 0, false));

        pieceList.add(new Pawn(this, 0, 1, false));
        pieceList.add(new Pawn(this, 1, 1, false));
        pieceList.add(new Pawn(this, 2, 1, false));
        pieceList.add(new Pawn(this, 3, 1, false));
        pieceList.add(new Pawn(this, 4, 1, false));
        pieceList.add(new Pawn(this, 5, 1, false));
        pieceList.add(new Pawn(this, 6, 1, false));
        pieceList.add(new Pawn(this, 7, 1, false));

        pieceList.add(new Rook(this, 0, 7, true));
        pieceList.add(new Knight(this, 1, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Queen(this, 3, 7, true));
        pieceList.add(new King(this, 4, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));
        pieceList.add(new Knight(this, 6, 7, true));
        pieceList.add(new Rook(this, 7, 7, true));

        pieceList.add(new Pawn(this, 0, 6, true));
        pieceList.add(new Pawn(this, 1, 6, true));
        pieceList.add(new Pawn(this, 2, 6, true));
        pieceList.add(new Pawn(this, 3, 6, true));
        pieceList.add(new Pawn(this, 4, 6, true));
        pieceList.add(new Pawn(this, 5, 6, true));
        pieceList.add(new Pawn(this, 6, 6, true));
        pieceList.add(new Pawn(this, 7, 6, true));

    }


    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //Paint Board
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                g2d.setColor((c + r) % 2 == 0 ? new Color(220, 210, 173) : new Color(56, 40, 10));
                g2d.fillRect(c * titleSize, r * titleSize, titleSize, titleSize);
            }
        //Paint HighLights
        if (selectedPiece != null)
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(26, 199, 199, 171));
                        g2d.fillRect(c * titleSize, r * titleSize, titleSize, titleSize);
                    }
                }

        //Paint Pieces
        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }
    }
}




class Piece {

    public int col,row;
    public int xPos,yPos;
    public boolean isWhite;
    public String name;
    public int value;
    public boolean isFirstMove = true;

    BufferedImage sheet;
    {
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("Chess_Pieces_Sprite.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected int sheetScale = sheet.getWidth()/6;

    Image sprite;

    Board board;

    public Piece (Board board) {
        this.board = board;
    }

    public boolean isValidMovement(int col,int row){
        return true;
    }

    public boolean moveCollidesWithPiece(int col,int row){
        return false;
    }

    public void paint(Graphics2D g2d){
        g2d.drawImage(sprite,xPos,yPos,null);
    }
}




class Input extends MouseAdapter {
    Board board;

    public Input(Board board){
        this.board = board;

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = e.getX()/board.titleSize;
        int row = e.getY()/board.titleSize;

        Piece pieceXY = board.getPiece(col,row);
        if (pieceXY != null){
            board.selectedPiece = pieceXY;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = e.getX()/board.titleSize;
        int row = e.getY()/board.titleSize;

        if(board.selectedPiece != null){
            Move move = new Move(board,board.selectedPiece,col ,row);

            if(board.isValidMove(move)){
                board.makeMove(move);
            } else {
                board.selectedPiece.xPos = board.selectedPiece.col * board.titleSize;
                board.selectedPiece.yPos = board.selectedPiece.row * board.titleSize;
            }
        }
        board.selectedPiece = null;
        board.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (board.selectedPiece != null){
            board.selectedPiece.xPos = e.getX() - board.titleSize / 2;
            board.selectedPiece.yPos = e.getY() - board.titleSize / 2;

            board.repaint();
        }
    }
}




class Move {
    int oldCol;
    int oldRow;

    int newCol;
    int newRow;

    Piece piece;
    Piece capture;

    public Move(Board board,Piece piece,int newCol,int newRow){
        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;

        this.piece = piece;
        this.capture = board.getPiece(newCol,newRow);

    }
}


class Scanner {
    Board board;
    public Scanner(Board board){
        this.board = board;
    }
    public boolean isKingScanned(Move move){

        Piece king = board.findKing((move.piece.isWhite));
        assert king != null;

        int kingCol = king.col;
        int kingRow = king.row;

        if(board.selectedPiece != null && board.selectedPiece.name.equals("King")){
            kingCol = move.newCol;
            kingRow = move.newRow;
        }
        return hitByRook(move.newCol,move.newRow,king,kingCol,kingRow,0,1)||  // up
                hitByRook(move.newCol,move.newRow,king,kingCol,kingRow,1,0)|| // right
                hitByRook(move.newCol,move.newRow,king,kingCol,kingRow,0,-1)|| //down
                hitByRook(move.newCol,move.newRow,king,kingCol,kingRow,-1,0)||  //left

                hitByBishop(move.newCol,move.newRow,king,kingCol,kingRow,-1,-1)|| //up left
                hitByBishop(move.newCol,move.newRow,king,kingCol,kingRow,1,-1)||  // up right
                hitByBishop(move.newCol,move.newRow,king,kingCol,kingRow,1,1)||   // down right
                hitByBishop(move.newCol,move.newRow,king,kingCol,kingRow,-1,1)|| // down left

                hitByKnight(move.newCol,move.newRow,king,kingCol,kingRow)||
                hitByPawn(move.newCol,move.newRow,king,kingCol,kingRow)||
                hitByKing(king,kingCol,kingRow);
    }


    private boolean hitByRook(int col,int row,Piece king,int kingcol,int kingRow,int colVal,int rowVal){
        for(int i = 1;i < 0;i++){
            if(kingcol + (i*colVal)==col && kingRow + (i*rowVal)==row){
                break;
            }
            Piece piece = board.getPiece(kingcol+(i*colVal),kingRow + (i*rowVal));
            if (piece != null && piece != board.selectedPiece){
                if(!board.sameTeam(piece,king)&&(piece.name.equals("Rook")|| piece.name.equals("Queen"))){
                    return true;
                }
                break;
            }
        }
        return false;
    }


    private boolean hitByBishop(int col,int row,Piece king,int kingcol,int kingRow,int colVal,int rowVal){
        for(int i = 1;i < 0;i++){
            if(kingcol - (i*colVal)==col && kingRow - (i*rowVal)==row){
                break;
            }
            Piece piece = board.getPiece(kingcol-(i*colVal),kingRow - (i*rowVal));
            if (piece != null && piece != board.selectedPiece){
                if(!board.sameTeam(piece,king)&&(piece.name.equals("Bishop")|| piece.name.equals("Queen"))){
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean hitByKnight(int col,int row,Piece king,int kingCol,int kingRow){
        return checkKnight(board.getPiece(kingCol - 1,kingRow - 2),king,col,row) ||
                checkKnight(board.getPiece(kingCol + 1,kingRow - 2),king,col,row) ||
                checkKnight(board.getPiece(kingCol + 2,kingRow - 1),king,col,row) ||
                checkKnight(board.getPiece(kingCol + 2,kingRow + 1),king,col,row) ||
                checkKnight(board.getPiece(kingCol + 1,kingRow + 2),king,col,row) ||
                checkKnight(board.getPiece(kingCol - 1,kingRow + 2),king,col,row) ||
                checkKnight(board.getPiece(kingCol - 2,kingRow + 1),king,col,row) ||
                checkKnight(board.getPiece(kingCol - 2,kingRow - 1),king,col,row) ;

    }
    private boolean checkKnight(Piece p,Piece k,int col,int row){
        return p != null && !board.sameTeam(p,k) && p.name.equals("Knight")&& !(p.col == col && p.row == row);
    }



    private boolean hitByKing(Piece king,int kingCol,int kingRow){
        return checkKing(board.getPiece(kingCol - 1,kingRow -1),king) ||
                checkKing(board.getPiece(kingCol + 1,kingRow -1),king) ||
                checkKing(board.getPiece(kingCol ,kingRow -1),king) ||
                checkKing(board.getPiece(kingCol - 1,kingRow ),king) ||
                checkKing(board.getPiece(kingCol + 1,kingRow ),king) ||
                checkKing(board.getPiece(kingCol - 1,kingRow + 1),king) ||
                checkKing(board.getPiece(kingCol + 1,kingRow + 1),king) ||
                checkKing(board.getPiece(kingCol ,kingRow + 1),king);
    }
    private boolean checkKing(Piece p,Piece k){
        return p != null && !board.sameTeam(p,k)&& p.name.equals("King");
    }


    private boolean hitByPawn(int col,int row,Piece king,int kingCol,int kingRow){
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(board.getPiece(kingCol + 1,kingRow + colorVal ),king,col,row) ||
                checkPawn(board.getPiece(kingCol - 1,kingRow + colorVal ),king,col,row) ;
    }
    private boolean checkPawn(Piece p,Piece k,int col,int row){
        return p != null && !board.sameTeam(p,k)&& p.name.equals("Pawn")&& !(p.col == col && p.row == row);
    }
}




class Pawn extends Piece {
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "Pawn";

        this.sprite = sheet.getSubimage(5 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale)
                .getScaledInstance(board.titleSize, board.titleSize, BufferedImage.SCALE_SMOOTH);

    }

    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        //Push 1
        if (this.col == col && row == this.row - colorIndex && board.getPiece(col, row) == null)
            return true;
        //Push 2
        if (isFirstMove && this.col == col && row == this.row - colorIndex * 2 &&
                board.getPiece(col, row) == null && board.getPiece(col, row + colorIndex) == null)
            return true;
        //Capture left
        if (col == this.col - 1 && row == this.row - colorIndex && board.getPiece(col, row) != null)
            return true;
        //Capture right
        if (col == this.col + 1 && row == this.row - colorIndex && board.getPiece(col, row) != null)
            return true;

        //Pass Left
        if (board.getTileNum(col, row) == board.PassTile && col == this.col - 1
                && row == this.row - colorIndex
                && board.getPiece(col, row + colorIndex) != null){
            return true;
        }
        //Pass Right
        if (board.getTileNum(col, row) == board.PassTile && col == this.col + 1
                && row == this.row - colorIndex
                && board.getPiece(col, row + colorIndex) != null){
            return true;
        }
        return false;
    }
}



class Knight extends Piece{
    public Knight(Board board,int col,int row,boolean isWhite){
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "Knight";

        this.sprite = sheet.getSubimage(3 * sheetScale, isWhite ? 0 : sheetScale,sheetScale,sheetScale)
                .getScaledInstance(board.titleSize,board.titleSize, BufferedImage.SCALE_SMOOTH);

    }
    public boolean isValidMovement(int col,int row){
        return Math.abs(col - this.col)* Math.abs(row - this.row) == 2;
    }
}



class Bishop extends Piece {
    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "Bishop";

        this.sprite = sheet.getSubimage(2 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale)
                .getScaledInstance(board.titleSize, board.titleSize, BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }
    public boolean moveCollidesWithPiece(int col, int row) {
        //Up left
        if (this.col > col && this.row > row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col - i, this.row - i) != null)
                    return true;
        //Up right
        if (this.col < col && this.row > row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col + i, this.row - i) != null)
                    return true;

        //Down left
        if (this.col > col && this.row < row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col - i, this.row + i) != null)
                    return true;
        //down rig
        if (this.col < col && this.row < row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col + i, this.row + i) != null)
                    return true;
        return false;
    }
}




class Rook extends Piece{
    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "Rook";

        this.sprite = sheet.getSubimage(4 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale)
                .getScaledInstance(board.titleSize, board.titleSize, BufferedImage.SCALE_SMOOTH);
    }
    public boolean isValidMovement ( int col, int row){
        return this.col == col || this.row == row;
    }
    public boolean moveCollidesWithPiece(int col,int row){
        //Left
        if(this.col > col)
            for (int c = this.col - 1;c > col;c--)
                if(board.getPiece(c,this.row) != null)
                    return true;
        //Right
        if(this.col < col)
            for (int c = this.col + 1;c < col;c++)
                if(board.getPiece(c,this.row) != null)
                    return true;
        //Up
        if(this.row > row)
            for (int r = this.row - 1;r > row;r--)
                if(board.getPiece(this.col,r) != null)
                    return true;
        //Down
        if(this.row < row)
            for (int r = this.row + 1;r < row;r++)
                if(board.getPiece(this.col,r) != null)
                    return true;
        return false;
    }
}



 class Queen extends Piece{
    public Queen(Board board, int col, int row, boolean isWhite){
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "Queen";

        this.sprite = sheet.getSubimage(sheetScale, isWhite ? 0 : sheetScale,sheetScale,sheetScale)
                .getScaledInstance(board.titleSize,board.titleSize, BufferedImage.SCALE_SMOOTH);

    }
    public boolean isValidMovement ( int col, int row){
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }
    public boolean moveCollidesWithPiece(int col,int row){
        if(this.col == col || this.row == row) {
            //Left
            if (this.col > col)
                for (int c = this.col - 1; c > col; c--)
                    if (board.getPiece(c, this.row) != null)
                        return true;
            //Right
            if (this.col < col)
                for (int c = this.col + 1; c < col; c++)
                    if (board.getPiece(c, this.row) != null)
                        return true;
            //Up
            if (this.row > row)
                for (int r = this.row - 1; r > row; r--)
                    if (board.getPiece(this.col, r) != null)
                        return true;
            //Down
            if (this.row < row)
                for (int r = this.row + 1; r < row; r++)
                    if (board.getPiece(this.col, r) != null)
                        return true;
        }else{
            //Up left
            if (this.col > col && this.row > row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (board.getPiece(this.col - i, this.row - i) != null)
                        return true;
            //Up right
            if (this.col < col && this.row > row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (board.getPiece(this.col + i, this.row - i) != null)
                        return true;

            //Down left
            if (this.col > col && this.row < row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (board.getPiece(this.col - i, this.row + i) != null)
                        return true;
            //down rig
            if (this.col < col && this.row < row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (board.getPiece(this.col + i, this.row + i) != null)
                        return true;
        }
        return false;
    }
}


class King extends Piece{
    public King(Board board, int col, int row, boolean isWhite){
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.titleSize;
        this.yPos = row * board.titleSize;

        this.isWhite = isWhite;
        this.name = "King";

        this.sprite = sheet.getSubimage(0 * sheetScale, isWhite ? 0 : sheetScale,sheetScale,sheetScale)
                .getScaledInstance(board.titleSize,board.titleSize, BufferedImage.SCALE_SMOOTH);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs((col - this.col)*(row - this.row)) == 1 ||
                Math.abs(col - this.col)+Math.abs(row - this.row)==1 || Castle(col,row);
    }
    private boolean Castle(int col,int row) {
        if(this.row == row)
            if(col == 6){
                Piece rook = board.getPiece(7,row);
                if (rook != null && rook.isFirstMove && isFirstMove){
                    return board.getPiece(5,row)== null &&
                            board.getPiece(6,row)== null &&
                            !board.scanner.isKingScanned(new Move(board,this,5,row));
                }
            } else {
                if(col == 2){
                    Piece rook = board.getPiece(0,row);
                    if (rook != null && rook.isFirstMove && isFirstMove){
                        return board.getPiece(3,row)== null &&
                                board.getPiece(2,row)== null &&
                                board.getPiece(1,row)== null &&
                                !board.scanner.isKingScanned(new Move(board,this,3,row));
                    }
                }
            }
        return false;
    }
}



