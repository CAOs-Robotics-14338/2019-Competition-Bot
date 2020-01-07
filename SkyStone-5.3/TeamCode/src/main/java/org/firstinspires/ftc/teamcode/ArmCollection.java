package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmCollection {
    private Servo claw, wrist;
    private CRServo expansion;


///WRIST
    static final double INCREMENT   = 0.01;     // amount to slew servo each CYCLE_MS cycle
    static final double MAX_POS     =  1.0;     // Maximum rotational position
    static final double MIN_POS     =  0.0;     // Minimum rotational position
    double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position



    public ArmCollection(Servo claW, Servo wrisT, CRServo expansioN){
        claw = claW;
        wrist = wrisT;
        expansion = expansioN; /*    reel = hardwareMap.get(CRServo.class, "reel_servo");  **Should be continous servo*/
    }


//expansion

        public  void expandControl(double ystick){ //expansion should be continous servo
            double expandRange = Range.clip( (-ystick), -1.0, 1.0);
            if (expandRange > 0){
                expansion.setPower(1);
            }
            else {
                if (expandRange < 0){
                    expansion.setPower(-1);                }
                else
                    expansion.setPower(0);
            }

    }


//claw
    public void grab(boolean button){
        if (button){
            claw.setPosition(0.9);
        }
    }
    public void release(boolean button) {
        if (button) {
            claw.setPosition(0.3);
        }
    }
    //wrist

    public void wristControl(double xstick) {
        double wristRange = Range.clip( (-xstick), -1.0, 1.0);
        if (wristRange > 0){
            position += INCREMENT ;
            if (position >= MAX_POS ) {
                position = MAX_POS;
            }
        }
        else {
            if (wristRange < 0){
                position = -1.0;
                position -= INCREMENT ;
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                }
            }
        }
        wrist.setPosition(position);
    }


}