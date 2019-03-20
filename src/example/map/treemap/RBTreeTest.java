package example.map.treemap;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @Author: jiangys
 * @Description:
 * @Date: Created in 14:03 2019/3/13
 * @Modified By:
 */
public class RBTreeTest {

    private RBTree<Integer> tree = new RBTree<>();

    @Before
    public void before(){
        tree.insert(12);
        tree.insert(7);
        tree.insert(13);
        tree.insert(21);
        tree.insert(15);
        tree.insert(2);
        tree.insert(4);
        tree.insert(33);
        tree.insert(1);
        tree.insert(27);
        tree.insert(17);
    }

    /**
     * 测试红黑树的插入
     */
    @Test
    public void testInsert(){
//        RBTree<Integer> tree = new RBTree<>();
        tree.printTree();
    }

    /**
     * 测试红黑树的删除
     */
    @Test
    public void delete(){
        tree.delete(12);
        tree.printTree();
    }


    @Test
    public void testUncle(){
        int count = 100;
        while (count > 0) {
            int i = (int) (Math.random() * 10000);
            tree.insert(i);
            count--;
        }
        tree.printTree();
    }
}
