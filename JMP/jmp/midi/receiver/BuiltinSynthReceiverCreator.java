package jmp.midi.receiver;

import javax.sound.midi.Receiver;

import jmp.core.JMPCore;
import jmp.midi.NullReceiver;

class BuiltinSynthReceiverCreator extends ReceiverCreator {

    @Override
    public Receiver getReciever() {
        // 新しいJMSynthインスタンスを取得する
        Receiver reciever = (Receiver)JMPCore.getSystemManager().newBuiltinSynthInstance();
        if (reciever == null) {
            /* 例外処理 */
            JMPCore.getWindowManager().disposeBuiltinSynthFrame();
            reciever = new NullReceiver();
        }
        return reciever;
    }

}
