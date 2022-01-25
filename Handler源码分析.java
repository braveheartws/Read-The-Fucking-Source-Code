1.一个线程对应一个looper
2.handler是生产者消费者模型
3.MessageQueue是优先级队列
4.Message缓存池个数最大50
5.handler为什么会造成内存泄漏

part1.Handler基本使用
public final int MSG_TEST = 5;
Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_TEST:
                    // do something
                    break;
                default:
                    // do something
                    break;
            }
        }
    };

Message msg = Message.obtain();
msg.what = MSG_TEST;
handler.sendMessage(msg);

part2.源码分析
handler.sendMessage();
	sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
		enqueueMessage(queue, msg, uptimeMillis);
			msg.target = this;
			queue.enqueueMessage(msg, uptimeMillis);

enqueueMessage@MessageQueue
	Message p = mMessages;
    boolean needWake;
    if (p == null || when == 0 || when < p.when) {
        // 没有消息，将新消息插入链表头
        msg.next = p;
        mMessages = msg;
        needWake = mBlocked;
    } else {
        needWake = mBlocked && p.target == null && msg.isAsynchronous();
        // 找到消息的位置
        Message prev;
        for (;;) {
            prev = p;
            p = p.next;
            if (p == null || when < p.when) {
                break;
            }
            if (needWake && p.isAsynchronous()) {
                needWake = false;
            }
        }
        // 将消息插入到指定位置
        msg.next = p; // invariant: p == prev.next
        prev.next = msg;
    }
loop()@Looper
	// 找到当前线程对应的looper
	Looper me = myLooper();
	// 获取looper绑定的消息队列
	MessageQueue queue = me.mQueue;
	for (;;) {
		// 获取下一个消息
		Message msg = queue.next(); // might block
		// 分发消息到对应的handler去执行
		msg.target.dispatchMessage(msg);
			if (msg.callback != null) {
            	handleCallback(msg);	// 如果消息设置了callback直接由其callback执行
        	} else {
	            if (mCallback != null) {
	                if (mCallback.handleMessage(msg)) {
	                    return;
	                }
	            }
	            // 由handler的handleMessage执行
            	handleMessage(msg);
        	}
		// 将消息放到缓存池
		msg.recycleUnchecked();
	}
next()@MessageQueue
	for (;;) {
		// 休眠操作：nextPollTimeoutMillis休眠时长，-1：一直休眠
		nativePollOnce(ptr, nextPollTimeoutMillis);
		synchronized (this) {
			Message prevMsg = null;
			Message msg = mMessages;
			if (msg != null && msg.target == null) {
				// 找到异步消息
				do {
					prevMsg = msg;
					msg = msg.next;
				} while (msg != null && !msg.isAsynchronous());
			}
			if (msg != null) {
				if (now < msg.when) {
                        // 下一条消息尚未准备好。设置超时，以便在准备就绪时唤醒。
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                } else {
                        // Got a message.
                        mBlocked = false;
                        // 当前一个消息不为空，则将前一个消息指向当前的下一个消息，返回当前消息
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        msg.markInUse();
                        return msg;
                }
			}
		}	else {
            // 没有消息时设置一直休眠
            nextPollTimeoutMillis = -1;
        }
        // 当执行下一次循环时 线程阻塞在nativePollOnce(ptr, nextPollTimeoutMillis);
        
	}