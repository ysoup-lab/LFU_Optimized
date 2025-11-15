import java.util.HashMap;
import java.util.Map;

class LFUCache {
    // 节点类
    static class Node {
        int key, value, freq;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }
    
    // 频率链表类
    static class FreqList {
        int freq;
        Node head, tail;
        int size;
        
        FreqList(int freq) {
            this.freq = freq;
            this.head = new Node(0, 0);
            this.tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
            this.size = 0;
        }
        
        // 在链表头部插入节点（最新访问）
        void addFirst(Node node) {
            Node next = head.next;
            head.next = node;
            node.prev = head;
            node.next = next;
            next.prev = node;
            size++;
        }
        
        // 删除指定节点
        void remove(Node node) {
            Node prev = node.prev;
            Node next = node.next;
            prev.next = next;
            next.prev = prev;
            size--;
        }
        
        // 删除最旧的节点（尾部）
        Node removeLast() {
            if (size == 0) return null;
            Node last = tail.prev;
            remove(last);
            return last;
        }
        
        boolean isEmpty() {
            return size == 0;
        }
    }
    
    private int capacity;
    private int minFreq;
    private Map<Integer, Node> keyNodeMap;
    private Map<Integer, FreqList> freqListMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyNodeMap = new HashMap<>();
        this.freqListMap = new HashMap<>();
    }

    public int get(int key) {
        if (capacity == 0 || !keyNodeMap.containsKey(key)) {
            return -1;
        }
        
        Node node = keyNodeMap.get(key);
        // 更新节点频率
        increaseFreq(node);
        
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        
        if (keyNodeMap.containsKey(key)) {
            // 键已存在，更新值和频率
            Node node = keyNodeMap.get(key);
            node.value = value;
            increaseFreq(node);
        } else {
            // 键不存在，需要插入新节点
            if (keyNodeMap.size() >= capacity) {
                // 容量已满，删除最不常用且最旧的节点
                FreqList minFreqList = freqListMap.get(minFreq);
                Node removedNode = minFreqList.removeLast();
                keyNodeMap.remove(removedNode.key);
                
                // 如果该频率链表为空，移除该频率
                if (minFreqList.isEmpty()) {
                    freqListMap.remove(minFreq);
                }
            }
            
            // 创建新节点
            Node newNode = new Node(key, value);
            keyNodeMap.put(key, newNode);
            
            // 将新节点加入频率为1的链表
            FreqList freq1List = freqListMap.getOrDefault(1, new FreqList(1));
            freq1List.addFirst(newNode);
            freqListMap.put(1, freq1List);
            
            // 新节点的频率为1，所以minFreq重置为1
            minFreq = 1;
        }
    }
    
    // 增加节点的频率
    private void increaseFreq(Node node) {
        int oldFreq = node.freq;
        FreqList oldFreqList = freqListMap.get(oldFreq);
        
        // 从旧频率链表中移除节点
        oldFreqList.remove(node);
        
        // 如果旧频率链表为空且是当前最小频率，更新minFreq
        if (oldFreqList.isEmpty()) {
            freqListMap.remove(oldFreq);
            if (oldFreq == minFreq) {
                minFreq++;
            }
        }
        
        // 更新节点频率
        int newFreq = oldFreq + 1;
        node.freq = newFreq;
        
        // 将节点加入新频率链表
        FreqList newFreqList = freqListMap.getOrDefault(newFreq, new FreqList(newFreq));
        newFreqList.addFirst(node);
        freqListMap.put(newFreq, newFreqList);
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

