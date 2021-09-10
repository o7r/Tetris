package tetris;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Field extends JPanel{

    //シングルトンパターンを使用。
    private static final Field thisPanel = new Field();
    public static Field getInstance() {
        return thisPanel;
    }

    //縦が４列多いのは、その４列（０～３列目）がテトロミノの生成箇所などであるため。
    public static final int lengthX = 10, lengthY = 24;
    //ピクセルサイズ。枠線がブロックの上に上書きされるため、blockSizeから２引いたものが実質的なブロックのサイズとなる。
    public static final int blockSize = 28;

    //色が存在すればそこにその色のブロックが存在する。nullであれば存在しない。
    private Color[][] fieldBlocks = new Color[lengthX][lengthY];
    //現在のテトロミノ。
    private Tetromino tetromino;
    private final Timer timer;


    private Field() {
        setBackground(ColorManager.THIN);
        setSize(blockSize * lengthX + 1, blockSize * 24 + 1);
        setLocation(20, 20);

        this.timer = new Timer(1000, e -> {
            //テトロミノを一つ下に下げ、再描画。もしそれができなければ新しいテトロミノの生成とそれに伴う処理を実行。
            if(this.tetromino.oneDrop()) {
                repaint();
            } else {
                nextTetrominoProcess();
            }
        });
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //０～３列目の背景色を描画。
        g.setColor(ColorManager.DARK);
        g.fillRect(0, 0, 10 * blockSize, 4 * blockSize);

        if (this.tetromino != null) {
            //テトロミノを描画。
            g.setColor(this.tetromino.getColor());
            Arrays.stream(this.tetromino.getBlocksLocation())
                    .forEach(i -> g.fillRect(i.x() * blockSize, i.y() * blockSize, blockSize, blockSize));

            //ハードドロップしたときのテトロミノの位置を描画。
            Tetromino.IndexOf2DArray[] locations = this.tetromino.getBlocksLocationIfDoHardDrop();
            if (locations != null) {
                Arrays.stream(locations)
                        .forEach(i -> g.drawRect(i.x() * blockSize + 1, i.y() * blockSize + 1, blockSize - 2, blockSize - 2));
            }
        }

        //フィールドに存在するブロックを描画。
        for (int x = 0; x < lengthX; x++) {
            for (int y = 0; y < lengthY; y++) {
                if (this.fieldBlocks[x][y] != null) {
                    g.setColor(this.fieldBlocks[x][y]);
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }

        //枠線を描画。
        g.setColor(ColorManager.BLACK);
        Stream.iterate(0, x -> x + 1)
                .limit(lengthX + 1)
                .forEach(x -> g.drawLine(x * blockSize, 0, x * blockSize, lengthY * blockSize));
        Stream.iterate(0, y -> y + 1)
                .limit(lengthY + 1)
                .forEach(y -> g.drawLine(0, y * blockSize, lengthX * blockSize, y * blockSize));
    }

    public void gameStart() {
        gameEnd();
        this.tetromino = new Tetromino();
        this.timer.start();
    }

    public void gameEnd() {
        this.timer.stop();
        this.fieldBlocks = new Color[lengthX][lengthY];
        this.tetromino = null;
        repaint();
        //this.timer.setDelay(100);
    }

    public boolean isAllValidArrayIndexInField(Tetromino.IndexOf2DArray... indexes) {
        if (indexes == null || indexes.length == 0 || Arrays.stream(indexes).anyMatch(Objects::isNull)) {
            return false;
        }
        //引数として渡されたfieldの位置を表す変数のすべてが配列内であればtrueを返す。
        return Arrays.stream(indexes)
                .noneMatch(i -> i.x() < 0 || i.y() < 0 || Field.lengthX - 1 < i.x() || Field.lengthY - 1 < i.y());
    }

    public boolean isNotPlaceable(Tetromino.IndexOf2DArray... indexes) {
        if (indexes == null || indexes.length == 0) {
            return false;
        }
        //引数として渡されたfieldのいずれかの位置に、既にブロックが存在すればtrueを返す。
        return Arrays.stream(indexes)
                .filter(Objects::nonNull)
                .filter(this::isAllValidArrayIndexInField)
                .anyMatch(i -> this.fieldBlocks[i.x()][i.y()] != null);
    }

    public Tetromino getTetromino() {
        return this.tetromino;
    }

    public void nextTetrominoProcess() {
        //既にあるテトロミノのすべてのブロックをフィールドに追加。
        if (this.tetromino != null) {
            Arrays.stream(this.tetromino.getBlocksLocation())
                    .forEach(i -> this.fieldBlocks[i.x()][i.y()] = this.tetromino.getColor());
        }

        //消せる列を削除し、その分列を下に下げる。
        deleteAndDownRows();

        //追加されたブロックの位置が全て０～３列目にあればゲームオーバーとする。
        boolean gameOver = Arrays.stream(this.tetromino.getBlocksLocation())
                .allMatch(i -> i.y() < 4);

        if (!gameOver) {
            //新しいテトロミノを生成、その位置に既に一つでもブロックが置かれていればゲームオーバーとする。
            this.tetromino = new Tetromino();
            if (isNotPlaceable(this.tetromino.getBlocksLocation())) {
                gameOver = true;
            }
        }

        //ゲームオーバーの処理。
        if (gameOver) {
            this.timer.stop();
            this.tetromino = null;
            //フィールドに置かれているブロックを全て特定の色で染める。
            for (int x = 0; x < lengthX; x++) {
                for (int y = 0; y < lengthY; y++) {
                    if (this.fieldBlocks[x][y] != null) {
                        this.fieldBlocks[x][y] = ColorManager.BLACK;
                    }
                }
            }
        }
        repaint();
    }

    public void deleteAndDownRows() {
        //消すことができる列（すべてにブロックがある列）の配列番号を集約。
        List<Integer> canDeleteRows = Stream.iterate(0, y -> y + 1)
                .limit(lengthY)
                .filter(y -> Stream.iterate(0, x -> x + 1)
                        .limit(lengthX)
                        .allMatch(x -> this.fieldBlocks[x][y] != null))
                .toList();

        //消すことができる列がなければメソッドを中断。
        if (canDeleteRows.size() == 0) {
            return;
        }

        //消すことができる列のブロックをすべて削除。
        canDeleteRows.forEach(y -> IntStream.iterate(0, x -> x + 1)
                        .limit(lengthX)
                        .forEach(x -> this.fieldBlocks[x][y] = null));

        //列を下に下げる処理。
        //処理の性質上、下の列から順に行う、ただし一番下の列が下に下がることはないためそこは飛ばす。
        for (int y = lengthY - 2; -1 < y; y--) {
            //対象列より下にある消えた列の数を取得。
            final int deleteCount = Math.toIntExact(Stream.iterate(y + 1, i -> i + 1)
                    .limit(lengthY)
                    .filter(canDeleteRows::contains)
                    .count());

            //対象列より下に消えた列がなければ、対象列への処理を中断し、次の列へ。
            if (deleteCount == 0) {
                continue;
            }

            //対象列のブロックをすべて、対象列より下にある消えた列の数だけ下にある位置にコピーする。
            final int finalY = y;
            Stream.iterate(0, x -> x + 1)
                    .limit(lengthX)
                    .forEach(x -> this.fieldBlocks[x][finalY + deleteCount] = this.fieldBlocks[x][finalY]);

            //対象列のブロックをすべて削除。
            Stream.iterate(0, x -> x + 1)
                    .limit(lengthX)
                    .forEach(x -> this.fieldBlocks[x][finalY] = null);
        }
        repaint();
    }
}