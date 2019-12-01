package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ScissorLift {
    static final double MOTOR_TICK_COUNT = 1120; //1440
    private DcMotor ScissorLiftMotor;
    int initial = 560; //This is the amount that we need to raise the scissor lift to compensate for the foundation height
    int block = 1120; //This is the amount that the scissor lift must rise for 1 block
    int position = 0;


    public ScissorLift(DcMotor ScissorLift){
        ScissorLiftMotor = ScissorLift;



    }
    public  void LiftControl(double y2){
        double ScissorLiftPower = Range.clip( (-y2), -1.0, 1.0);
        ScissorLiftMotor.setPower(ScissorLiftPower);

    }
    public void LiftMovement(boolean scissorUp, boolean scissorDown, boolean reset, boolean activate){
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


    }

}
