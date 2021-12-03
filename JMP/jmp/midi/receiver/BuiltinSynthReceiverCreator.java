package jmp.midi.receiver;

import javax.sound.midi.Receiver;

import jmp.core.JMPCore;

class BuiltinSynthReceiverCreator extends ReceiverCreator {

    @Override
    public Receiver getReciever() {
        // 新しいJMSynthインスタンスを取得する
        Receiver reciever = JMPCore.getSystemManager().newBuiltinSynthInstance();
        if (reciever == null) {
            /* 例外処理 */
            JMPCore.getWindowManager().disposeBuiltinSynthFrame();

            // Nullレシーバーを返す
            NoneReceiverCreator none = new NoneReceiverCreator();
            reciever = none.getReciever();
        }
        return reciever;
    }

}
