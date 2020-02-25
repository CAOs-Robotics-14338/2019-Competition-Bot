package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

public class Capstone {
    // Setup variables and devices
    Servo cap, claw;
    ArmCollection armCollection;
    private CRServo expansion;

    //Set Up Intake pulley for the automated tasks
    private Servo IntakePulley;

    private double extended = 0;
    private double retracted = 1;


    // Constructor for capstone servo
    public Capstone(Servo capstone, Servo cla){
        cap = capstone;
        claw = cla;

    }

    // Method for extending the capstone by pressing one button and retracting it by pressing a different button
    public void CapControl(boolean EnabledState, boolean DisabledState){

        if(EnabledState){
            cap.setPosition(extended);
            claw.setPosition(0.0);
        }

        else if(DisabledState){
            cap.setPosition(retracted);
        }


    }

}
