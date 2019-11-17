package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class ScissorLift {
    private DcMotor ScissorLiftMotor;

        public ScissorLift(DcMotor ScissorLift){
            ScissorLiftMotor = ScissorLift;



        }
        public  void LiftControl(double y2){
            double ScissorLiftPower = Range.clip( (-y2), -1.0, 1.0);

            ScissorLiftMotor.setPower(ScissorLiftPower);

        }

}
