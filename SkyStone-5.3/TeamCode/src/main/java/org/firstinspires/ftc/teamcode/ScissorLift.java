package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ScissorLift {
    static final double MOTOR_TICK_COUNT = 1120; //1440
    private DcMotor ScissorLiftMotorLeft, ScissorLiftMotorRight;
    private Servo LinearSlideServo1, LinearSlideServo2,  ClawServo;
    int initial = 560; //This is the amount that we need to raise the scissor lift to compensate for the foundation height
    int block = 1120; //This is the amount that the scissor lift must rise for 1 block
    int pos = 0;
    boolean pressed = false;
    int target = 0;
    double slideServoActive = -1;
    double slideServoStored = -1;
    double clawServoActive = -1;
    double clawServoStored = -1;


        public ScissorLift(DcMotor ScissorLiftL, DcMotor ScissorLiftR, Servo Claw, Servo Slide1, Servo Slide2){
            ScissorLiftMotorLeft = ScissorLiftL;
            ScissorLiftMotorRight = ScissorLiftR;
            LinearSlideServo1 = Slide1;
            LinearSlideServo2 = Slide2;
            ClawServo = Claw;




        }
        public void ClawControl(boolean button1, boolean button2, double C2RTS){
            double SlidePos = Range.clip( (-C2RTS), slideServoStored, slideServoActive );
            LinearSlideServo1.setPosition(SlidePos);
            LinearSlideServo2.setPosition(SlidePos);
            if(button1){
                ClawServo.setPosition(clawServoStored);
            }
            if(button2){
                ClawServo.setPosition(clawServoActive);
            }



        }
        public  void LiftControl(double y2){
            double ScissorLiftPower = Range.clip( (-y2), -1.0, 1.0);
            ScissorLiftMotorLeft.setPower(ScissorLiftPower);
            ScissorLiftMotorRight.setPower(ScissorLiftPower);

        }
        public  void LiftControlTest(double y2, boolean left, boolean right, boolean up, boolean down, boolean b2){
            double ScissorLiftPower = Range.clip( (-y2), -1.0, 1.0);
            if(y2 != 0 && !pressed) {
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                ScissorLiftMotorLeft.setPower(ScissorLiftPower);
                ScissorLiftMotorRight.setPower(ScissorLiftPower);
            }
            if(!left && !right && !up && !down){
                pressed = false;
            }
            if(up && !pressed){
                pressed = true;
                pos += 1;
            }
            if(down && !pressed && pos > 0){
                pressed = true;
                pos -= 1;
            }
            if(right && !pressed){
                pressed = true;
                target = ((pos * block) + initial);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorLeft.setTargetPosition(target);
                ScissorLiftMotorRight.setTargetPosition(target);
                ScissorLiftMotorLeft.setPower(0.6);
                ScissorLiftMotorRight.setPower(0.6);

                while(ScissorLiftMotorLeft.isBusy() || ScissorLiftMotorRight.isBusy()){}
                ScissorLiftMotorLeft.setPower(0);
                ScissorLiftMotorRight.setPower(0);

            }
            if(left && !pressed){
                pressed = true;
                target = ((pos * block) + initial);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                ScissorLiftMotorLeft.setPower(-0.3);
                ScissorLiftMotorRight.setPower(-0.3);
                while(ScissorLiftMotorLeft.getCurrentPosition() > 0 || ScissorLiftMotorRight.getCurrentPosition() > 0){}
                ScissorLiftMotorLeft.setPower(0);
                ScissorLiftMotorRight.setPower(0);

            }
            if(b2 && !pressed){
                pressed = true;
                target =  initial;
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorLeft.setTargetPosition(initial);
                ScissorLiftMotorRight.setTargetPosition(initial);
                ScissorLiftMotorLeft.setPower(0.6);
                ScissorLiftMotorRight.setPower(0.6);
                while(ScissorLiftMotorLeft.isBusy() || ScissorLiftMotorRight.isBusy()){}
                ScissorLiftMotorLeft.setPower(0);
                ScissorLiftMotorRight.setPower(0);

            }
    }

}
