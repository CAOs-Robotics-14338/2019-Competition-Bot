package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "SkyClaw", group = "Auto")
public class Claw_Auto extends LinearOpMode {

    Servo Sky_Claw;
    private ElapsedTime runtime = new ElapsedTime();

    // Declaring the DC motors for our holonomic drive base
    private DcMotor FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor;
    HolonomicDrive holonomicDrive;

    // Starting OPMode
    @Override
    public void runOpMode() {

        // Classifying our Dc motors with their names on the expansion hub
        FrontRightMotor = hardwareMap.get(DcMotor.class, "front_right_drive");
        FrontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_drive");
        BackRightMotor = hardwareMap.get(DcMotor.class, "back_right_drive");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "back_left_drive");

        // Setting our holonomic drive to use our 2 front and 2 back motors
        holonomicDrive = new HolonomicDrive(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor);

        // Classifying our servos with their names on the expansion hub
        FrontRightMotor.setPower(0.5);
        sleep(2000);
        FrontLeftMotor.setPower(0.5);
        sleep(2000);
        BackRightMotor.setPower(0.5);
        sleep(2000);
        BackLeftMotor.setPower(0.5);
        sleep(2000);




    }
}
