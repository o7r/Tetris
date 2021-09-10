package tetris;

import java.awt.Color;
import java.util.Random;

public final class Tetromino {

    //以下の３つの変数を用いてfieldにおけるテトロミノ全体の位置を保持する。

    //fieldにおけるテトロミノの回転軸の位置
    private IndexOf2DArray rotationAxis = new IndexOf2DArray(5, 1);
    //テトロミノの回転パターンのうちのどれであるか。
    private int rotationForm = 0;
    //LやI型など、テトロミノの形。
    private final Shape shape = switch(new Random().nextInt(7)) {
        default -> Shape.I;
        case 1 -> Shape.L;
        case 2 -> Shape.J;
        case 3 -> Shape.Z;
        case 4 -> Shape.S;
        case 5 -> Shape.T;
        case 6 -> Shape.O;
    };


    public Color getColor() {
        return this.shape.color;
    }

    public IndexOf2DArray[] getBlocksLocation() {
        //fieldにおけるテトロミノの位置を算出。
        return getBlocksLocation(this.rotationAxis, this.rotationForm);
    }

    public IndexOf2DArray[] getBlocksLocationIfDoHardDrop() {
        //ハードドロップしたときのテトロミノの位置を算出。
        IndexOf2DArray[] locations = null;
        for (int y = this.rotationAxis.y;; y++) {
            IndexOf2DArray[] i = getBlocksLocation(new IndexOf2DArray(this.rotationAxis.x, y), this.rotationForm);
            if (i == null || Field.getInstance().isNotPlaceable(i)) {
                return locations;
            }
            locations = i;
        }
    }

    public void moveLeft() {
        changeIfPossible(new IndexOf2DArray(this.rotationAxis.x - 1, this.rotationAxis.y), this.rotationForm);
    }

    public void moveRight() {
        changeIfPossible(new IndexOf2DArray(this.rotationAxis.x + 1, this.rotationAxis.y), this.rotationForm);
    }

    public boolean oneDrop() {
        return changeIfPossible(new IndexOf2DArray(this.rotationAxis.x, this.rotationAxis.y + 1), this.rotationForm);
    }

    public void hardDrop() {
        while(oneDrop()) {
        }
        Field.getInstance().nextTetrominoProcess();
    }

    public void rotateLeft() {
        changeIfPossible(this.rotationAxis, switch(this.rotationForm) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> 0;
        });
    }

    public void rotateRight() {
        changeIfPossible(this.rotationAxis, switch(this.rotationForm) {
            case 0 -> 3;
            case 3 -> 2;
            case 2 -> 1;
            default -> 0;
        });
    }

    private boolean changeIfPossible(IndexOf2DArray rotationAxis, int rotationForm) {
        IndexOf2DArray[] locations = getBlocksLocation(rotationAxis, rotationForm);
        //変更後のテトロミノの位置を表す変数に配列範囲外があった（locationsがnull）か、既にブロックが存在すれば置けないと判断し、処理を中断。
        if (locations == null || Field.getInstance().isNotPlaceable(locations)) {
            return false;
        }

        //テトロミノの位置などを変更し、fieldを再描画。
        this.rotationAxis = rotationAxis;
        this.rotationForm = rotationForm;
        Field.getInstance().repaint();
        return true;
    }

    private IndexOf2DArray[] getBlocksLocation(IndexOf2DArray rotationAxis, int rotationForm) {
        //テトロミノの回転軸の位置、回転パターンの２つを引数として受け取り、それらを用いてfieldにおけるテトロミノ全体の位置を算出。
        //テトロミノの位置を表す変数のいずれかにfieldの配列範囲外があればnullを返す。
        IndexOf2DArray[] result = switch(this.shape) {
            /* case I */
            default -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 2, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y)
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 2)
                };
                case 2 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x + 2, rotationAxis.y),
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 2),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
            };
            case L -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y)
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 2 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y + 1)
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y + 1)
                };
            };
            case J -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y + 1)
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 2 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y)
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y + 1),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
            };
            case Z -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y)
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y + 1)
                };
                case 2 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y + 1)
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
            };
            case S -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 2 -> new IndexOf2DArray[] {
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y + 1),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y + 1)
                };
            };
            case T -> switch(rotationForm) {
                default -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y)
                };
                case 1 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 2 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
                case 3 -> new IndexOf2DArray[] {
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                        rotationAxis,
                        new IndexOf2DArray(rotationAxis.x + 1, rotationAxis.y),
                        new IndexOf2DArray(rotationAxis.x, rotationAxis.y + 1)
                };
            };
            case O -> new IndexOf2DArray[] {
                    new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y - 1),
                    new IndexOf2DArray(rotationAxis.x, rotationAxis.y - 1),
                    new IndexOf2DArray(rotationAxis.x - 1, rotationAxis.y),
                    rotationAxis
            };
        };

        //テトロミノの位置を表す変数のいずれかにfieldの配列範囲外があればnullを返す。
        return Field.getInstance().isAllValidArrayIndexInField(result) ? result : null;
    }


    public record IndexOf2DArray(int x, int y) {}


    private enum Shape {
        I(ColorManager.LIGHT_BLUE),
        L(ColorManager.ORANGE),
        J(ColorManager.BLUE),
        Z(ColorManager.RED),
        S(ColorManager.GREEN),
        T(ColorManager.PURPLE),
        O(ColorManager.YELLOW);

        private final Color color;

        Shape(Color color) {
            this.color = color;
        }
    }
}