import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

class LFUCache {
    // 缓存容量，时间戳
    int capacity, time;
    Map<Integer, Node> key_table;
    TreeSet<Node> S;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.time = 0;
        key_table = new HashMap<Integer, Node>();
        S = new TreeSet<Node>();
    }

    public int get(int key) {
        if (capacity == 0) {
            return -1;
        }
        // 如果哈希表中没有键 key，返回 -1
        if (!key_table.containsKey(key)) {
            return -1;
        }
        // 从哈希表中得到旧的缓存
        Node cache = key_table.get(key);
        // 从平衡二叉树中删除旧的缓存
        S.remove(cache);
        // 将旧缓存更新
        cache.cnt += 1;
        cache.time = ++time;
        // 将新缓存重新放入哈希表和平衡二叉树中
        S.add(cache);
        key_table.put(key, cache);
        return cache.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        if (!key_table.containsKey(key)) {
            // 如果到达缓存容量上限
            if (key_table.size() == capacity) {
                // 从哈希表和平衡二叉树中删除最近最少使用的缓存
                key_table.remove(S.first().key);
                S.remove(S.first());
            }
            // 创建新的缓存
            Node cache = new Node(1, ++time, key, value);
            // 将新缓存放入哈希表和平衡二叉树中
            key_table.put(key, cache);
            S.add(cache);
        } else {
            // 这里和 get() 函数类似
            Node cache = key_table.get(key);
            S.remove(cache);
            cache.cnt += 1;
            cache.time = ++time;
            cache.value = value;
            S.add(cache);
            key_table.put(key, cache);
        }
    }
    public static void main(String[] args) {
        // 测试用例1: 基本功能测试
        System.out.println("=== 测试用例1: 基本功能测试 ===");
        LFUCache cache1 = new LFUCache(2);

        cache1.put(1, 1);
        cache1.put(2, 2);
        System.out.println("get(1): " + cache1.get(1)); // 返回 1
        cache1.put(3, 3);    // 删除 key 2
        System.out.println("get(2): " + cache1.get(2)); // 返回 -1 (未找到)
        System.out.println("get(3): " + cache1.get(3)); // 返回 3
        cache1.put(4, 4);    // 删除 key 1
        System.out.println("get(1): " + cache1.get(1)); // 返回 -1 (未找到)
        System.out.println("get(3): " + cache1.get(3)); // 返回 3
        System.out.println("get(4): " + cache1.get(4)); // 返回 4

        System.out.println();

        // 测试用例2: 频率计数测试
        System.out.println("=== 测试用例2: 频率计数测试 ===");
        LFUCache cache2 = new LFUCache(3);

        cache2.put(1, 1);
        cache2.put(2, 2);
        cache2.put(3, 3);

        // 多次访问 key=1 增加其频率
        cache2.get(1);
        cache2.get(1);
        cache2.get(2);

        cache2.put(4, 4); // 应该删除 key=3 (频率最低)
        System.out.println("get(3): " + cache2.get(3)); // 应该返回 -1
        System.out.println("get(1): " + cache2.get(1)); // 应该返回 1
        System.out.println("get(2): " + cache2.get(2)); // 应该返回 2
        System.out.println("get(4): " + cache2.get(4)); // 应该返回 4

        System.out.println();

        // 测试用例3: 容量为0的边缘情况
        System.out.println("=== 测试用例3: 容量为0的边缘情况 ===");
        LFUCache cache3 = new LFUCache(0);
        cache3.put(1, 1);
        System.out.println("get(1): " + cache3.get(1)); // 应该返回 -1

        System.out.println();

        System.out.println("\n所有测试完成！");
    }
}

class Node implements Comparable<Node> {
    int cnt, time, key, value;

    Node(int cnt, int time, int key, int value) {
        this.cnt = cnt;
        this.time = time;
        this.key = key;
        this.value = value;
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Node) {
            Node rhs = (Node) anObject;
            return this.cnt == rhs.cnt && this.time == rhs.time;
        }
        return false;
    }

    public int compareTo(Node rhs) {
        return cnt == rhs.cnt ? time - rhs.time : cnt - rhs.cnt;
    }

    public int hashCode() {
        return cnt * 1000000007 + time;
    }
}

