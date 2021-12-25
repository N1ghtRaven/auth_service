package works.red_eye.hood.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.util.*;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class AuthLoader {
    public static void main(String[] args) {
        SpringApplication.run(AuthLoader.class, args);
    }

    static Node rootNode = new Node();
    static Map<String, int[][]> traceMap = new HashMap<>();
    static List<String> list = new ArrayList<>();

    public static void main2(String[] args) {
        String[][] array = fill();
        rootNode.setValue(array[0][0]);
        Node trace = trace(0, 0, array);
//        printTree(trace);
        List<String> strings = binaryTreePaths(trace);
        System.out.println(strings);
    }

    public static void printTree(Node rootNode) { // метод для вывода дерева в консоль
        Stack globalStack = new Stack(); // общий стек для значений дерева
        globalStack.push(rootNode);
        int gaps = 32; // начальное значение расстояния между элементами
        boolean isRowEmpty = false;
        String separator = "-----------------------------------------------------------------";
        System.out.println(separator);// черта для указания начала нового дерева
        while (isRowEmpty == false) {
            Stack localStack = new Stack(); // локальный стек для задания потомков элемента
            isRowEmpty = true;

            for (int j = 0; j < gaps; j++)
                System.out.print(' ');
            while (globalStack.isEmpty() == false) { // покуда в общем стеке есть элементы
                Node temp = (Node) globalStack.pop(); // берем следующий, при этом удаляя его из стека
                if (temp != null) {
                    System.out.print(temp.getValue()); // выводим его значение в консоли
                    localStack.push(temp.getLeftChild()); // соохраняем в локальный стек, наследники текущего элемента
                    localStack.push(temp.getRightChild());
                    if (temp.getLeftChild() != null ||
                            temp.getRightChild() != null)
                        isRowEmpty = false;
                }
                else {
                    System.out.print("__");// - если элемент пустой
                    localStack.push(null);
                    localStack.push(null);
                }
                for (int j = 0; j < gaps * 2 - 2; j++)
                    System.out.print(' ');
            }
            System.out.println();
            gaps /= 2;// при переходе на следующий уровень расстояние между элементами каждый раз уменьшается
            while (localStack.isEmpty() == false)
                globalStack.push(localStack.pop()); // перемещаем все элементы из локального стека в глобальный
        }
        System.out.println(separator);// подводим черту
    }

    static public List<String> binaryTreePaths(Node root) {
        ArrayList<String> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        if (root.getLeftChild() == null && root.getRightChild() == null) {
            res.add(root.getValue() + "");
            return res;
        }
        List<String> leftPaths = binaryTreePaths(root.getLeftChild());
        for (String s : leftPaths) {
            res.add(root.getValue() + "->" + s);
        }
        List<String> rightPaths = binaryTreePaths(root.getRightChild());
        for (String s : rightPaths) {
            res.add(root.getValue() + "->" + s);
        }
        return res;
    }

    static String[][] fill() {
        String[][] qq = new String[13][2];
        qq[0][0] = "A"; qq[0][1] = "M";
        qq[1][0] = "B"; qq[1][1] = "N";
        qq[2][0] = "C"; qq[2][1] = "O";
        qq[3][0] = "D"; qq[3][1] = "P";
        qq[4][0] = "E"; qq[4][1] = "Q";
        qq[5][0] = "F"; qq[5][1] = "R";
        qq[6][0] = "G"; qq[6][1] = "S";
        qq[7][0] = "H"; qq[7][1] = "T";
        qq[8][0] = "I"; qq[8][1] = "U";
        qq[9][0] = "J"; qq[9][1] = "V";
        qq[10][0] = "K"; qq[10][1] = "W";
        qq[11][0] = "L"; qq[11][1] = "X";

        return qq;
    }

    static Node trace(int x, int y, String[][] arr) {
        String s = arr[x][y];
        int[][] route = new int[2][2];

        if (x == 11) {
            route[0][0] = x - 1; route[0][1] = y;
        }
        else {
            route[0][0] = x + 1; route[0][1] = y;
        }

        if (y == 0) {
            route[1][0] = x; route[1][1] = y + 1;
        }
        else {
            route[1][0] = x; route[1][1] = y - 1;
        }



        Node node = new Node();
        node.setValue(s);


        try {
            String format = String.format("Трассирую \"%s\" - (%s;%s), (%s;%s)", s, arr[route[0][0]][route[0][1]], s, arr[route[1][0]][route[1][1]], s);

            if (!list.contains(format)) {
                System.out.println(format);
                list.add(format);
                node.setLeftChild(trace(route[0][0], route[0][1], arr));
                node.setRightChild(trace(route[1][0], route[1][1], arr));
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        return node;
    }

}