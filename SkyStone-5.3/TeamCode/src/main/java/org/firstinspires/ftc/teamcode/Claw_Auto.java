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
        Sky_Claw = hardwareMap.servo.get("Sky_Claw");

        Sky_Claw.setPosition(0.9);

        waitForStart();
/*        // driving backwards
        runtime.reset();
        FrontLeftMotor.setPower(-0.5);
        BackLeftMotor.setPower(-0.5);
        FrontRightMotor.setPower(0.5);
        BackRightMotor.setPower(0.5);
    while (opModeIsActive() && runtime.seconds() < 2){
            telemetry.addLine("Strafing right");
            telemetry.update();
        }
        holonomicDrive.stopMoving();

        Sky_Claw.setPosition(0.3);
        sleep(2000);*/

/*        // 90 degree left hand turn
        runtime.reset();
        FrontLeftMotor.setPower(0.5);
        BackLeftMotor.setPower(-0.5);
        FrontRightMotor.setPower(-0.5);
        BackRightMotor.setPower(0.5);
        while (opModeIsActive() && runtime.seconds() < 2){
            telemetry.addLine("Strafing left");
            telemetry.update();
        }
        holonomicDrive.stopMoving();*/
        runtime.reset();
        FrontLeftMotor.setPower(-0.5);
        BackLeftMotor.setPower(0.5);
        FrontRightMotor.setPower(0.5);
        BackRightMotor.setPower(-0.5);
        while (opModeIsActive() && runtime.seconds() < 2){
            telemetry.addLine("Strafing left");
            telemetry.update();
        }
        holonomicDrive.stopMoving();
        Sky_Claw.setPosition(0.9);
        sleep(2000);


/*        // turning clockwise
        runtime.reset();
        FrontLeftMotor.setPower(-0.5);
        BackLeftMotor.setPower(0.5);
        FrontRightMotor.setPower(-0.5);
        BackRightMotor.setPower(0.5);
        while (opModeIsActive() && runtime.seconds() < 2){
            telemetry.addLine("Strafing left");
            telemetry.update();
        }
        holonomicDrive.stopMoving();
        Sky_Claw.setPosition(0.9);
        sleep(2000);*/

/*
        runtime.reset();

        holonomicDrive.autoDrive(90.0, 2);
        while (opModeIsActive() && runtime.seconds() < 0.5){
            telemetry.addLine("auto strafing to the right");
            telemetry.update();
        }
        holonomicDrive.stopMoving();
        sleep(2000);

        runtime.reset();
        holonomicDrive.autoDrive(270.0, 2);
        while (opModeIsActive() && runtime.seconds() < 0.5){
            telemetry.addLine("auto strafing to the left");
            telemetry.update();
        }
        holonomicDrive.stopMoving();
        Sky_Claw.setPosition(0.3);
        sleep(2000);
*/

    }
}
