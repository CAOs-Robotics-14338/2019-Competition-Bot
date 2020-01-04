package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ScissorLift {
    static final double MOTOR_TICK_COUNT = 1120; //1440
    private DcMotor ScissorLiftMotorLeft, ScissorLiftMotorRight;
    int initial = 560; //This is the amount that we need to raise the scissor lift to compensate for the foundation height
    int block = 1120; //This is the amount that the scissor lift must rise for 1 block
    int pos = 0;
    boolean pressed = false;
    int target = 0;


        public ScissorLift(DcMotor ScissorLiftL, DcMotor ScissorLiftR){
            ScissorLiftMotorLeft = ScissorLiftL;
            ScissorLiftMotorRight = ScissorLiftR;



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
                ScissorLiftMotorLeft.setTargetPosition(target);
                ScissorLiftMotorRight.setTargetPosition(target);
                ScissorLiftMotorLeft.setPower(0.6);
                ScissorLiftMotorRight.setPower(0.6);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ScissorLiftMotorLeft.isBusy() || ScissorLiftMotorRight.isBusy()){}
                ScissorLiftMotorLeft.setPower(0);
                ScissorLiftMotorRight.setPower(0);

            }
            if(left && !pressed){
                pressed = true;
                target = ((pos * block) + initial);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ScissorLiftMotorLeft.setTargetPosition(-target);
                ScissorLiftMotorRight.setTargetPosition(-target);
                ScissorLiftMotorLeft.setPower(-0.6);
                ScissorLiftMotorRight.setPower(-0.6);
                ScissorLiftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ScissorLiftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ScissorLiftMotorLeft.isBusy() || ScissorLiftMotorRight.isBusy()){}
                ScissorLiftMotorLeft.setPower(0);
                ScissorLiftMotorRight.setPower(0);

            }
            if(b2 && !pressed){

            }
    }
/*        public void LiftMovement(boolean scissorUp, boolean scissorDown, boolean reset, boolean activate){
            if(scissorUp){ position += 1; }
            if(scissorDown && position > 0){position -= 1;}
            ScissorLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            int newPos = ((position * block) + initial);
            if(activate) {
                ScissorLiftMotor.setTargetPosition(newPos);
                ScissorLiftMotor.setPower(0.95);
                while(ScissorLiftMotor.isBusy()){}
                ScissorLiftMotor.setPower(0);
            }

            int pos2 = ScissorLiftMotor.getTargetPosition() - newPos;
            if(reset){
                ScissorLiftMotor.setTargetPosition(pos2);
                ScissorLiftMotor.setPower(-0.95);
                while(ScissorLiftMotor.isBusy()){}
                ScissorLiftMotor.setPower(0);
                position = 0;

            }


        }*/

}
