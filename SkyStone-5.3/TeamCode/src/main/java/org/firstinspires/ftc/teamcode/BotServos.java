package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.Servo;


public class BotServos {
    Servo left_hook, right_hook;
    double lStored = 0;
    double rStored = 1;
    double lActive = 0.6;
    double rActive = 0.4;

    public BotServos(Servo lHook, Servo rHook){
        left_hook = lHook;
        right_hook = rHook;

    }

        public void retract(boolean a1, boolean b1) {
            if (a1 || b1) {
                left_hook.setPosition(lStored);
                right_hook.setPosition(rStored);

            }
        }
            public void retract ( boolean a1){
                if (a1 ) {
                    left_hook.setPosition(lStored);
                    right_hook.setPosition(rStored);

                }

            }

            public void auto(boolean a2){
                if(a2){
                    left_hook.setPosition(lActive);
                    right_hook.setPosition(rActive);
                }
                else{
                    left_hook.setPosition(lStored);
                    right_hook.setPosition(rStored);
                }
            }
            public void activate ( boolean y1, boolean y2){
                if (y1 || y2) {
                    left_hook.setPosition(lActive);
                    right_hook.setPosition(rActive);

                }

            }

            public void activate ( boolean y1){
                if (y1 ) {
                    left_hook.setPosition(lActive);
                    right_hook.setPosition(rActive);

                }

            }



}
