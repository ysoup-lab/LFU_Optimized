import java.util.HashMap;
import java.util.Map;

class LFUCache {
    private int capacity;
    private Map<Integer, Node> keyToNode;
    private Map<Integer, DoublyLinkedList> freqToDList;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        keyToNode = new HashMap<>();
        freqToDList = new HashMap<>();
        minFreq = 0;
    }

    public int get(int key) {
        if (!keyToNode.containsKey(key)) {
            return -1;
        }
        Node node = keyToNode.get(key);
        updateFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            node.value = value;
            updateFreq(node);
            return;
        }
        if (keyToNode.size() >= capacity) {
            DoublyLinkedList minFreqList = freqToDList.get(minFreq);
            Node removedNode = minFreqList.removeLast();
            keyToNode.remove(removedNode.key);
        }
        Node newNode = new Node(key, value, 1);
        keyToNode.put(key, newNode);
        freqToDList.computeIfAbsent(1, k -> new DoublyLinkedList()).addFirst(newNode);
        minFreq = 1;
    }

    private void updateFreq(Node node) {
        int oldFreq = node.freq;
        DoublyLinkedList oldList = freqToDList.get(oldFreq);
        oldList.remove(node);
        if (oldList.size() == 0 && oldFreq == minFreq) {
            minFreq++;
        }
        node.freq++;
        freqToDList.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).addFirst(node);
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

class Node {
    int key;
    int value;
    int freq;
    Node prev;
    Node next;

    public Node(int key, int value, int freq) {
        this.key = key;
        this.value = value;
        this.freq = freq;
        this.prev = null;
        this.next = null;
    }
}

class DoublyLinkedList {
    Node head;
    Node tail;
    int size;

    public DoublyLinkedList() {
        head = new Node(-1, -1, 0);
        tail = new Node(-1, -1, 0);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    public void addFirst(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    public void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    public Node removeLast() {
        if (size == 0) {
            return null;
        }
        Node last = tail.prev;
        remove(last);
        return last;
    }

    public int size() {
        return size;
    }
}

