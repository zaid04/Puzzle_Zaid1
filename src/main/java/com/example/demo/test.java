package com.example.demo;

class Test {
    char lettre;
    int x, y;

    public Test(char lettre, int x, int y) {
        this.lettre = lettre;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Test{" +
                "lettre=" + lettre +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public static void main(String[] args) {
        char lettre = 'd';
        Test[][] plateau = new Test[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                plateau[i][j] = new Test(lettre++, i, j);
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.println(plateau[i][j]);
            }
        }
    }
}
