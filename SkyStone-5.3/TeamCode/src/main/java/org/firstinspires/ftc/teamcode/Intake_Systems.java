package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake_Systems {
    private DcMotor IntakeRightMotor, IntakeLeftMotor;
    private Servo IntakePulley;
    double active = -1;
    double inactive = -1;

    public Intake_Systems(DcMotor rightIntake, DcMotor leftIntake, Servo pulley) {
        IntakeLeftMotor = leftIntake;
        IntakeRightMotor = rightIntake;
        IntakePulley = pulley;
    }
        public void intake(boolean collect, boolean deploy){
            if(collect == true){
                IntakeLeftMotor.setPower(0.95);
                IntakeRightMotor.setPower(-0.95);
            }
            else if(deploy == true){
                IntakeLeftMotor.setPower(-0.95);
                IntakeRightMotor.setPower(0.95);
            }
            else{
                IntakeLeftMotor.setPower(0);
                IntakeRightMotor.setPower(0);
            }

        }
        public void pullBackCollectionArms(){IntakePulley.setPosition(inactive);}
        public void releaseCollectionArms(){IntakePulley.setPosition(active);}

        public void stopCollection(){
            IntakeLeftMotor.setPower(0);
            IntakeRightMotor.setPower(0);
        }




}
