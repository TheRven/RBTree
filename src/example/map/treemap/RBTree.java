package example.map.treemap;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jiangys
 * @Description: 使用代码实现红黑树
 * @Date: Created in 17:18 2019/3/12
 * @Modified By:
 */
public class RBTree<T extends Comparable<T>> {

    private Node<T> root;

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    /**
     * 红黑树的删除
     * @param value
     */
    public void delete(T value){
        Node<T> node = get(value);
        if (node != null) {
            deleteNode(node);
        }
    }

    private void deleteNode(Node<T> node) {
        // replace 表示删除后顶替上来的节点
        // parent 表示replace的父节点
        Node<T> parent = null, replace = null;
        // 根据二叉树的删除逻辑
        // 共有三种情况
        // 1. 删除的节点没有子节点
        // 2. 删除的节点有单子节点
        // 3. 删除的节点有双子节点
        if (node.left != null && node.right != null) {
            // 此为第三种情况，可以转换成前两种情况
            Node<T> minNode = findMinNode(node.right);
            // 将最小节点的值赋给要删除的节点，删除最小节点
            node.data = minNode.data;
            // 此处转换成了前两种情况，进行一次递归
            deleteNode(minNode);
            return;
        } else {
            // 前两种情况，单节点或没有子节点
            if (node.parent == null) {
                // 如果node节点是根节点
                // 找一个子节点替换根节点，此时即使没有子节点也无所谓，直接把root置为null
                this.root = node.left == null ? node.right : node.left;
                replace = this.root;
                if (this.root != null) {
                    this.root.parent = null;
                }
            } else {
                // node不是根节点
                Node<T> child = node.left == null ? node.right : node.left;
                if (node.parent.left == node) {
                    node.parent.left = child;
                } else {
                    node.parent.right = child;
                }
                if (child != null) {
                    child.parent = node.parent;
                }
                replace = child;
                // 使用node.parent是因为child有可能是NIL节点
                parent = node.parent;
            }
        }
        // 如果待删除的节点为红色，直接结束即可
        // 因为走到这一步的node只是单子节点和无子节点的
        // 单子节点不可能node为红色
        // 无子节点中红色直接删除不需要调整，黑色需要进行调整
        if (node.color == BLACK) {
            deleteFixUp(replace, parent);
        }
    }

    private void deleteFixUp(Node<T> replace, Node<T> parent) {
        // 设置replace节点的兄弟节点为brother
        Node<T> brother = null;
        // 如果replace节点为空，并且不是根节点，这是需要调整的情况，即此while循环调整无子节点的情况
        // 这里面replace.color == BLACK的判断是条件是为了后续的循环调整
        while ((replace == null || replace.color == BLACK) && replace != this.root) {
            if (parent.left == replace) {
                brother = parent.right;
                // 判断几个兄弟节点的情况
                // case 1 : 兄弟节点为红色，那么parent必然为黑色
                if (brother.color == RED) {
                    // 此时交换brother和parent的颜色，对P进行左旋
                    brother.color = BLACK;
                    parent.color = RED;
                    leftRotate(parent);
                    // 左旋完以后重新设置brother
                    brother = parent.right;
                }
                // case 1 走完后，brother会变为黑色
                // case 2 : 兄弟节点为黑色，且兄弟的两个孩子都为黑色
                if ((brother.left == null || brother.left.color == BLACK)
                        && (brother.right == null || brother.right.color == BLACK)) {
                    // 如果父节点为红色，则替换父节点和兄弟节点的颜色
                    if (parent.color == RED) {
                        brother.color = RED;
                        parent.color = BLACK;
                        break;
                    } else {
                        // 父节点为黑色，此时替换兄弟节点的颜色为红色
                        // 这种情况下，会使父节点分支上少了一个黑色节点，所以将replace设置为父节点，继续调整
                        brother.color = RED;
                        // 就是这一步，所以会导致replace是黑色，不然传进这个方法的replace全是NIL
                        replace = parent;
                        parent = replace.parent;
                    }
                } else {
                    // case 3 : 此时当兄弟节点为黑色，左子节点为红色
                    if (brother.left != null && brother.left.color == RED) {
                        brother.left.color = parent.color;
                        parent.color = BLACK;
                        rightRotate(brother);
                        leftRotate(parent);
                    } else if (brother.right != null && brother.right.color == RED) {
                    // case 4 : 兄弟节点为黑色，右子节点为红色
                        brother.color = parent.color;
                        parent.color = BLACK;
                        brother.right.color = BLACK;
                        leftRotate(parent);
                    }
                    break;
                }
            } else {
                // 对称情况
                brother = parent.left;
                // case 1 : 红兄
                if (brother.color == RED) {
                    brother.color = BLACK;
                    parent.color = RED;
                    rightRotate(parent);
                    brother = parent.left;
                }
                // case 2 : 黑兄，且兄弟节点的两个子节点均为黑色
                if ((brother.left == null || brother.left.color == BLACK)
                        && (brother.right == null || brother.right.color == BLACK)) {
                    // 判断父节点的颜色
                    if (parent.color == RED) {
                        // 父节点红色，替换BP颜色
                        brother.color = RED;
                        parent.color = BLACK;
                        break;
                    } else {
                        // 父节点为黑色，将兄弟节点置为红色，此时将父节点作为replace进行循环
                        brother.color = RED;
                        replace = parent;
                        parent = replace.parent;
                    }
                } else {
                    // case 3 : 黑兄，兄弟右孩子为红色
                    if (brother.right != null && brother.right.color == RED) {
                        brother.right.color = parent.color;
                        parent.color = BLACK;
                        leftRotate(brother);
                        rightRotate(parent);
                    } else if (brother.left != null && brother.left.color == RED) {
                        // case 4 : 黑兄，兄弟左孩子为红色
                        brother.color = parent.color;
                        parent.color = BLACK;
                        brother.left.color = BLACK;
                        rightRotate(parent);
                    }
                    return;
                }
            }

        }
        // 处理单子节点的情况
        if (replace != null) {
            replace.color = BLACK;
        }
    }


    /**
     * 二叉树的删除
     * @param value
     */
    public void remove(T value){
        if (value == null) {
            return;
        }
        // 先找到这个值对应的节点
        Node<T> node = get(value);
        if (node != null) {
            remove(node);
        }
    }

    private void remove(Node<T> node) {
        // 1. 根据二叉树的逻辑删除节点
        if (node.left == null && node.right == null) {
            // 没有子节点
            if (node.parent == null) {
                root = null;
            } else {
                if (node.parent.left == node) {
                    node.parent.left = null;
                } else {
                    node.parent.right = null;
                }
            }
        } else if (node.left != null && node.right == null) {
            // 只有左子节点
            if (node.parent == null) {
                root = node.left;
            } else {
                if (node.parent.left == node) {
                    node.parent.left = node.left;
                } else {
                    node.parent.right = node.left;
                }
                node.left.parent = node.parent;
            }
        } else {
            // 只有右子节点或者左右子节点都有
            // 此时需要获取右子节点的最小节点替换掉当前要删的节点
            // 1. 获取右子节点的最小节点
            Node<T> minNode = findMinNode(node.right);
            // 2. 把要删除的节点的值改成这个最小节点的值
            node.data = minNode.data;
            // 3. 删除最小节点
            remove(minNode);
        }

    }

    /**
     * 判断当前树中是否存在元素
     * @param value
     * @return
     */
    public boolean contains(T value){
        if (value == null) {
            return false;
        }
        return contains(value, root);
    }

    private boolean contains(T value, Node<T> node) {
        if (node == null) {
            return false;
        }
        int result = node.data.compareTo(value);
        if (result > 0) {
            return contains(value, node.left);
        } else if (result < 0) {
            return contains(value, node.right);
        } else {
            return true;
        }
    }

    /**
     * 找到当前节点的最小子节点
     * @param t
     * @return
     */
    private Node<T> findMinNode(Node<T> t) {
        if (t == null) {
            return null;
        } else if (t.left == null) {
            return t;
        }
        return findMinNode(t.left);
    }


    /**
     * 获取值传入值对应的节点
     * @param value
     * @return
     */
    public Node<T> get(T value) {
        return get(value, root);
    }

    private Node<T> get(T value, Node<T> t) {
        if (t == null || value == null) {
            return null;
        }
        int result = t.data.compareTo(value);
        if (result > 0) {
            return get(value, t.left);
        } else if (result < 0) {
            return get(value, t.right);
        } else {
            return t;
        }
    }

    /**
     * 红黑树的插入操作
     */
    public void insert(T value){
        if (value == null) {
            return;
        }
        // 将红黑树作为一颗二叉查找树插入
        // 将插入的节点着色为红色
        Node<T> node = new Node<>(value, RED, null, null, null);
        this.root = insert(node, this.root);
        // 通过一系列的旋转着色操作，使其重新成为一颗红黑树
        insertFixUp(node);
    }

    /**
     * 二叉树的插入
     */
    private Node<T> insert(Node<T> node, Node<T> root) {
        if (node == null) {
            return null;
        }
        if (root == null) {
            root = node;
            return root;
        }
        int result = node.data.compareTo(root.data);
        if (result > 0) {
            node.parent = root;
            root.right = insert(node, root.right);
        } else if (result < 0) {
            node.parent = root;
            root.left = insert(node, root.left);
        }
        return root;
    }

    /**
     * 1. 如果插入的节点是根节点，也就是说初始的红黑树为空，或者插入的值和根节点相同，直接将该节点标记为黑色
     * @param node
     */
    private void fixUp1(Node<T> node){
        if (node.parent == null) {
            node.color = BLACK;
            return;
        } else {
            fixUp2(node);
        }
    }

    /**
     * 2. 如果插入的不是根节点，但是父节点是黑色的话，是不需要做任何改动的
     * @param node
     */
    private void fixUp2(Node<T> node) {
        if (isBlack(node.parent)) {
            return;
        } else {
            fixUp3(node);
        }
    }

    /**
     * 3. 如果插入的父节点是红色的话，需要进行调整
     *    如果叔叔节点存在且为红色
     * @param node
     */
    private void fixUp3(Node<T> node) {
        // 如果插入的父亲节点是红色的，那么它的祖父节点必然存在且是黑色的
        Node<T> parent = node.parent;
        Node<T> gParent = parent.parent;
        // 获取当前节点的叔叔节点
        Node<T> uncle = gParent.left == parent ? gParent.right : gParent.left;
        // 如果当前的叔叔节点存在且为红色
        if (uncle != null && isRed(uncle)) {
            parent.color = BLACK;
            uncle.color = BLACK;
            gParent.color = RED;
            // 此时祖父节点变为红色，进行递归判断
            fixUp1(gParent);
        } else {
            fixUp4(node);
        }
    }

    /**
     * 4. 父节点为红色，如果叔叔节点为黑色
     * 如果是一颗正常的红黑树不可能存在这种情况，因为插入是向最底部插入的
     * (1)父亲节点为红色，叔叔节点为黑色成立的唯一情况是：
     *      在进行递归的过程中，祖父节点的父节点是红色的，祖父节点的叔叔节点是黑色的，此时满足条件
     * (2)另一种情况是叔叔节点为空
     * 需要进行旋转
     */
    private void fixUp4(Node<T> node) {
        Node<T> parent = node.parent;
        Node<T> gParent = parent.parent;
        // 目前存在四种情况，
        // 1. 父节点为左子节点
        //        当前节点为左子节点
        //        当前节点为右子节点
        // 2. 父节点为右子节点
        //        ....
        // 把三个节点调整到一条直线上
        if (gParent.left == parent && parent.right == node) {
            leftRotate(parent);
            node = node.left;
        } else if (gParent.right == parent && parent.left == node) {
            rightRotate(parent);
            node = node.right;
        }
        // 重新指向新的parent和gParent
        parent = node.parent;
        gParent = parent.parent;
        // 调整父节点颜色黑色，祖父节点颜色为红色，在进行旋转
        parent.color = BLACK;
        gParent.color = RED;
        if (gParent.left == parent && parent.left == node) {
            rightRotate(gParent);
        } else if (gParent.right == parent && parent.right == node) {
            leftRotate(gParent);
        }
    }

    /**
     * 红黑树插入修正函数
     * @param node
     */
    private void insertFixUp(Node<T> node) {
        if (node == null) {
            return;
        }
        fixUp1(node);
    }

    private boolean isBlack(Node<T> node){
        return node == null ? false : node.color == BLACK;
    }

    private boolean isRed(Node<T> node){
        return node == null ? false : node.color == RED;
    }

    /**
     * 获取父节点
     * @param node
     * @return
     */
    private Node<T> getParent(Node<T> node){
        if (node == null) {
            return null;
        }
        return node.parent;
    }


    /**
     * 左旋操作
     */
    private void leftRotate(Node<T> x){
        if (x == null) {
            return;
        }
        // 对x节点进行左旋操作
        // 1.将x的右节点设置为y
        Node<T> y = x.right;

        if (y == null) {
            return;
        }
        // 2.将y的左子节点移动到x的右子节点
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }

        // 3.将x的父节点设置为y的父节点
        y.parent = x.parent;
        if (x.parent == null) {
            // 如果x的父节点为null，说明是根节点
            this.root = y;
        } else {
            // 有父节点，则对父节点的子节点进行设置
            if (x.parent.left == x) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        }

        // 4.将x设置为y的子节点
        x.parent = y;
        y.left = x;
    }

    /**
     * 右旋操作
     */
    private void rightRotate(Node<T> y){
        if (y == null) {
            return;
        }
        Node<T> x = y.left;
        if (x == null) {
            return;
        }
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == null) {
            this.root = x;
        } else {
            if (y.parent.left == y) {
                y.parent.left = x;
            } else {
                y.parent.right = x;
            }
        }

        y.parent = x;
        x.right = y;
    }

    public void printTree(){
        if (root == null) {
            return;
        }
        System.out.println("根节点为:[" + root.data + "|" + root.getColor() + "]");
        System.out.println("----------------------");
        printTree(root);
    }

    private void printTree(Node<T> node) {
        if (node == null) {
            return;
        }
        System.out.println("[" + node.data + "|" +node.getColor() + "]");
        if (node.left != null) {
            System.out.println("左子节点为:[" + node.left.data + "|" +node.left.getColor() + "]");
        }
        if (node.right != null) {
            System.out.println("右子节点为:[" + node.right.data + "|" +node.right.getColor() + "]");
        }
        System.out.println("----------------------");
        printTree(node.left);
        printTree(node.right);
    }


    private class Node<T> {
        private T data;
        private boolean color;
        Node<T> left;
        Node<T> right;
        Node<T> parent;


        public Node(T data, boolean color, Node<T> left, Node<T> right, Node<T> parent) {
            this.data = data;
            this.color = color;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }

        public String getColor(){
            return this.color ? "黑色" : "红色";
        }

    }




}
