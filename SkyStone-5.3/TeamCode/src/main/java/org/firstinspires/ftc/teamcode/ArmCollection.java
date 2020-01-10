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

    private Servo IntakePulley;
    double active = 0;//1
    double inactive = 1; //0

///WRIST
    static final double INCREMENT   = 0.001;                         // amount to slew servo each CYCLE_MS cycle
    static final double MAX_POS     =  1.0;      //0.55                  // Maximum rotational position
    static final double MID_POS     = 0.45;       //0.35                  //Middle init position
    static final double MIN_POS     =  0.0;      //0.15                  // Minimum rotational position
    double position = MID_POS;





    public ArmCollection(Servo claW, Servo wrisT, CRServo expansioN, Servo pulley){
        claw = claW;
        wrist = wrisT;
        expansion = expansioN; /*    reel = hardwareMap.get(CRServo.class, "reel_servo");  **Should be continous servo*/
        wrist.setPosition(MID_POS);

        IntakePulley = pulley;


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

    public  void expandControlDPAD(boolean up, boolean down){ //expansion should be continous servo

        if (up){
            IntakePulley.setPosition(inactive);
            expansion.setPower(1);
        }
        else {

            if (down){
                IntakePulley.setPosition(inactive);
                expansion.setPower(-1);                }
            else
                expansion.setPower(0);
        }

    }


//claw
    public void grab(boolean button){
        if (button){
            IntakePulley.setPosition(inactive);
            claw.setPosition(0.0);          // preferred set 0.0
        }
    }
    public void release(boolean button) {
        if (button) {
            IntakePulley.setPosition(active);
            claw.setPosition(0.50);        // preferred set 0.50
        }
    }
    //wrist

    public void wristControl(double xstick, boolean right_bumper, boolean left_bumper, boolean start) {
        double wristRange = Range.clip( (-xstick), -1.0, 1.0);
        if (wristRange > 0){
            position += INCREMENT ;
            if (position >= MAX_POS ) {
                position = MAX_POS;
            }
        }
        else {
            if (wristRange < 0){
                //position = -1.0;
                position -= INCREMENT ;
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                }
            }
        }
        if (right_bumper){
            position = MIN_POS;

        }
        if (left_bumper){
            position = MAX_POS;
        }
        if (start){
            position = MID_POS;
        }
        wrist.setPosition(position);
    }
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
                //position = -1.0;
                position -= INCREMENT ;
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                }
            }
        }

        wrist.setPosition(position);
    }

    public void reset(boolean button){
        // set wrist to straight ahead and up and collector engaged and ready to collect

        // driver needs to retract expansion and lower the scissor lift possible code for later competition


    }




}