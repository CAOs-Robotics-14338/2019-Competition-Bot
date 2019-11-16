package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import java.lang.Math;

public class CollectorSystem {
    DcMotor intakeLeftMotor;
    DcMotor intakeRightMotor;

    Servo intakePulley;
    int backPos = -1;
    int releasedPos = -1;


   public CollectorSystem ( DcMotor LeftCollect, DcMotor RightCollect, Servo pulley ){
       intakeLeftMotor = LeftCollect;
       intakeRightMotor = RightCollect;
       intakePulley = pulley;
   }
    public void intake(boolean collectButton, boolean deployButton){
        if(collectButton == true){
            intakeLeftMotor.setPower(0.75);
            intakeRightMotor.setPower(-0.75);

        }
        else if (deployButton == true){
            intakeLeftMotor.setPower(-0.75);
            intakeRightMotor.setPower(0.75);

        }
        else{
            intakeLeftMotor.setPower(0);
            intakeRightMotor.setPower(0);

        }

    }

    public void pullBackCollectArms(){
       intakePulley.set(backPos);
    }
    public void releaseCollectArms(){
       intakePulley.set(releasedPos);
    }

    public void stopCollection(){
       intakeLeftMotor.set(0);
       intakeRightMotor.set(0);


    }
}
