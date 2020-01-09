package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake_Systems {
    private DcMotor IntakeRightMotor, IntakeLeftMotor;
    private Servo IntakePulley;
    double active = 0;//1
    double inactive = 1; //0

    public Intake_Systems(DcMotor rightIntake, DcMotor leftIntake, Servo pulley) {
        IntakeLeftMotor = leftIntake;
        IntakeRightMotor = rightIntake;
        IntakePulley = pulley;
    }
        public void intakeTele(boolean collect1, boolean deploy1, boolean collect2, boolean deploy2){
        if(collect1 || collect2){
            IntakeLeftMotor.setPower(0.95);
            IntakeRightMotor.setPower(-0.95);
        }
        else if(deploy1 || deploy2){
            IntakeLeftMotor.setPower(-0.95);
            IntakeRightMotor.setPower(0.95);
        }
        else{
            IntakeLeftMotor.setPower(0);
            IntakeRightMotor.setPower(0);
        }

    }
    public void intake(boolean collect1, boolean deploy1){
        if(collect1){
            IntakeLeftMotor.setPower(0.95);
            IntakeRightMotor.setPower(-0.95);
        }
        else if(deploy1){
            IntakeLeftMotor.setPower(-0.95);
            IntakeRightMotor.setPower(0.95);
        }
        else{
            IntakeLeftMotor.setPower(0);
            IntakeRightMotor.setPower(0);
        }

    }
        public void pullBackCollectionArms(boolean retract1, boolean retract2){
            if(retract1 || retract2){
                IntakePulley.setPosition(inactive);}

        }
    public void pullBackCollectionArms(boolean retract1){
        if(retract1 ){
            IntakePulley.setPosition(inactive);}

    }

        public void releaseCollectionArms(boolean release1, boolean release2){
            if(release1 || release2){
                IntakePulley.setPosition(active);}

        }
    public void releaseCollectionArms(boolean release1){
        if(release1){
            IntakePulley.setPosition(active);}

    }
        public void stopCollection(){
            IntakeLeftMotor.setPower(0);
            IntakeRightMotor.setPower(0);
        }




}
