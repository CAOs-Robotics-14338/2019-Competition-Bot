package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import java.lang.Math;

//This year we used a holonomic drive in order to be able to move in all directions.
//To look at all the math calculations that contributed to the design of the code, check out the Engineering Notebook Section.

public class HolonomicDrive {

    //Set up Hardware Devices and Device Rotation Direction
    String motorRotationDirection;
    DcMotor FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor;
    //Motor Speed Variable for the slower setting
    double slow_speed = 0.1; //0.5
   // double top_speed = 1.0;

    //Basic Constructor to Set-Up the Holonomic Drive that sets the motor direction to clockwise.
    public HolonomicDrive(DcMotor FrontRight, DcMotor FrontLeft, DcMotor BackRight, DcMotor BackLeft){
        motorRotationDirection = "CLOCKWISE";
        FrontRightMotor = FrontRight;
        FrontLeftMotor = FrontLeft;
        BackRightMotor = BackRight;
        BackLeftMotor = BackLeft;
    }


//Constructor for Setting the MotorDirection
    //This one will set the motor direction to either "COUNTER-CLOCKWISE" or "CLOCKWISE"
    public HolonomicDrive(String motorDirection, DcMotor FrontRight, DcMotor FrontLeft, DcMotor BackRight, DcMotor BackLeft){
        if(motorDirection.equals("COUNTER-CLOCKWISE")){
            motorRotationDirection = "COUNTER-CLOCKWISE";
            FrontRightMotor = FrontRight;
            FrontLeftMotor = FrontLeft;
            BackRightMotor = BackRight;
            BackLeftMotor = BackLeft;

        }
        else {//"CLOCKWISE"
            motorRotationDirection = "CLOCKWISE";
            FrontRightMotor = FrontRight;
            FrontLeftMotor = FrontLeft;
            BackRightMotor = BackRight;
            BackLeftMotor = BackLeft;

        }
    }

    //If for some reason, you need to change the motor rotation direction
    public void setMotorRotationDirection(String motorRotationDirection) {
        this.motorRotationDirection = motorRotationDirection;
    }
// If you need to get the Motor Rotation Direction
    public String getMotorRotationDirection() {
        return motorRotationDirection;
    }

    //Apply the teleop formula to each of the motors based on the motor direction
    public void teleopDrive(double x, double y, double z){
        //if clockwise do this direction
        if(this.getMotorRotationDirection().equals("CLOCKWISE")){
            //Get Formula Values  for each motor
            double FrontRightMotorPower = Range.clip( (x - y + z), -1.0, 1.0);
            double FrontLeftMotorPower = Range.clip( (x + y + z), -1.0, 1.0);
            double BackRightMotorPower = Range.clip( (-x - y + z), -1.0, 1.0);
            double BackLeftMotorPower = Range.clip( (-x + y + z), -1.0, 1.0);
            //Set the Motors to the Formulaic Values
            FrontRightMotor.setPower(FrontRightMotorPower);
            FrontLeftMotor.setPower(FrontLeftMotorPower);
            BackRightMotor.setPower(BackRightMotorPower);
            BackLeftMotor.setPower(BackLeftMotorPower);
        }
        //If counter-clockwise multiply the equations by -1 to get use for counter clockwise moving
        // motors.
        else{//COUNTER-CLOCKWISE
            //Get the associated formula values for each motor
            double FrontRightMotorPower = Range.clip( -(x - y + z), -1.0, 1.0);
            double FrontLeftMotorPower = Range.clip( -(x + y + z), -1.0, 1.0);
            double BackRightMotorPower = Range.clip( -(-x - y + z), -1.0, 1.0);
            double BackLeftMotorPower = Range.clip( -(-x + y + z), -1.0, 1.0);
        // Set the motors to the formula values
            FrontRightMotor.setPower(FrontRightMotorPower);
            FrontLeftMotor.setPower(FrontLeftMotorPower);
            BackRightMotor.setPower(BackRightMotorPower);
            BackLeftMotor.setPower(BackLeftMotorPower);
        }
    }
    // Method for Driving Robot in Teleop with slwo trigger
    public void teleopDrive(double x, double y, double z, double slow_trigger){
       if (slow_trigger>0){//If slow trigger pressed
           //Get the formula values for each motor using the max and min as the slow speed.
           if(this.getMotorRotationDirection().equals("CLOCKWISE")){
               double FrontRightMotorPower = Range.clip( (x - y + z), -slow_speed , slow_speed);
               double FrontLeftMotorPower = Range.clip( (x + y + z), -slow_speed , slow_speed);
               double BackRightMotorPower = Range.clip( (-x - y + z), -slow_speed , slow_speed);
               double BackLeftMotorPower = Range.clip( (-x + y + z), -slow_speed , slow_speed);

               FrontRightMotor.setPower(FrontRightMotorPower);
               FrontLeftMotor.setPower(FrontLeftMotorPower);
               BackRightMotor.setPower(BackRightMotorPower);
               BackLeftMotor.setPower(BackLeftMotorPower);
           }
           //For Counter-Clockwise:
           else{//COUNTER-CLOCKWISE
               //Get the formula values for each motor using the max and min as the slow speed.
               double FrontRightMotorPower = Range.clip( -(x - y + z), -slow_speed , slow_speed);
               double FrontLeftMotorPower = Range.clip( -(x + y + z), -slow_speed , slow_speed);
               double BackRightMotorPower = Range.clip( -(-x - y + z), -slow_speed , slow_speed);
               double BackLeftMotorPower = Range.clip( -(-x + y + z), -slow_speed , slow_speed);
                //Set the motors to the power from the formula
               FrontRightMotor.setPower(FrontRightMotorPower);
               FrontLeftMotor.setPower(FrontLeftMotorPower);
               BackRightMotor.setPower(BackRightMotorPower);
               BackLeftMotor.setPower(BackLeftMotorPower);
           }
       }
    }

//Method used for Demo-ing
    //Makes the maximum speed for entire drive 60 %
    public void teleopDriveDEMO(double x, double y, double z){
        if(this.getMotorRotationDirection().equals("CLOCKWISE")){
            double FrontRightMotorPower = Range.clip( (x - y + z), -0.6, 0.6);
            double FrontLeftMotorPower = Range.clip( (x + y + z),-0.6, 0.6);
            double BackRightMotorPower = Range.clip( (-x - y + z), -0.6, 0.6);
            double BackLeftMotorPower = Range.clip( (-x + y + z), -0.6, 0.6);

            FrontRightMotor.setPower(FrontRightMotorPower);
            FrontLeftMotor.setPower(FrontLeftMotorPower);
            BackRightMotor.setPower(BackRightMotorPower);
            BackLeftMotor.setPower(BackLeftMotorPower);
        }
        else{//COUNTER-CLOCKWISE
            double FrontRightMotorPower = Range.clip( -(x - y + z), -0.6, 0.6);
            double FrontLeftMotorPower = Range.clip( -(x + y + z), -0.6, 0.6);
            double BackRightMotorPower = Range.clip( -(-x - y + z), -0.6, 0.6);
            double BackLeftMotorPower = Range.clip( -(-x + y + z), -0.6, 0.6);

            FrontRightMotor.setPower(FrontRightMotorPower);
            FrontLeftMotor.setPower(FrontLeftMotorPower);
            BackRightMotor.setPower(BackRightMotorPower);
            BackLeftMotor.setPower(BackLeftMotorPower);
        }
    }


    //Look at Software Section of Engineering Notebook to see how the Math was found.
    public void autoDrive(double directionDegrees, double maxSpeed){
        //Get Formula Values
        double plusX = (((Math.PI)/4.0) + ((Math.PI)/180 * directionDegrees));
        double minusX = (((Math.PI)/4.0) - ((Math.PI)/180 * directionDegrees));
        double FrontRightMotorPower = -maxSpeed * Math.cos(plusX);
        double FrontLeftMotorPower = maxSpeed * Math.cos(minusX);
        double BackRightMotorPower = -maxSpeed * Math.cos(minusX);
        double BackLeftMotorPower = maxSpeed * Math.cos(plusX);
    // Set Motors to the Calculated Values
        FrontRightMotor.setPower(FrontRightMotorPower);
        FrontLeftMotor.setPower(FrontLeftMotorPower);
        BackRightMotor.setPower(BackRightMotorPower);
        BackLeftMotor.setPower(BackLeftMotorPower);
    }



//Method for stopping movement of the robot
    public void stopMoving(){
        //set all drive motors equal to 0
        FrontRightMotor.setPower(0);
        FrontLeftMotor.setPower(0);
        BackRightMotor.setPower(0);
        BackLeftMotor.setPower(0);
    }


    }

