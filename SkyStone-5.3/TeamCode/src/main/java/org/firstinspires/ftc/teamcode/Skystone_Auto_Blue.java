
package org.firstinspires.ftc.teamcode;


/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;





@Autonomous(name = "Skystone Auto B", group = "Blue")
public class Skystone_Auto_Blue extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor;
    HolonomicDrive holonomicDrive;
    Servo Sky_Claw;





    private static final String VUFORIA_KEY =
            " AYDOawL/////AAABmRg/2IBfP0h/gFrTpRMdOcYUlX4rWD72D/Rt+L/Z9YGEQ7REsFBVqq4Yo2hvSJoTrPuVgyHDjjOLgurV9q00YLltcWipqHo1fFxXA45LZHu0ODYKzJ7SCdh/9l9vHtpry3jlefDGdO17owoxqDQMdFwxoAY82mWIm+PhgcKHljKOGXlkCRJnTrEBk4/ldzd6uKw8Y9FMsbNtDlvSW8F2fxPXvhI22mc34D/O0auF3esgHVMq+XND+Ncs6/su+0myu7jiZ7/O8zVFvC5WvuX2P8k8p4RkQQVaNhKerGNGBkmzxHYxJIPKWGwX5NXuO28dIEtZh1N0Bm5BRoSxATCe9DLN41rRufeps6VTC4EwzBC+\n";




    private VuforiaLocalizer vuforia;




    private TFObjectDetector tfod;
    WebcamName webcamName = null;

    @Override
    public void runOpMode() {

        FrontRightMotor = hardwareMap.get(DcMotor.class, "front_right_drive");
        FrontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_drive");
        BackRightMotor = hardwareMap.get(DcMotor.class, "back_right_drive");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "back_left_drive");

        holonomicDrive = new HolonomicDrive(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor);

        Sky_Claw = hardwareMap.servo.get("sky_claw");
        double skyStored = 0.9;
        double skyActive = 0.3;
        Sky_Claw.setPosition(skyStored);




        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        int pos = 1;
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }




        if (tfod != null) {
            tfod.activate();
        }




        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();



        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            if (recognition.getLabel() .equals("Skystone") || (pos == 3))
                            {
                                runtime.reset();
                                FrontRightMotor.setPower(-0.8);
                                FrontLeftMotor.setPower(-0.8);
                                BackRightMotor.setPower(-0.8);
                                BackLeftMotor.setPower(-0.8);
                                while (opModeIsActive() && runtime.seconds() < 3.0){
                                    // Adding telemetry of the time elapsed
                                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                                    telemetry.update();
                                }
                                // Stopping the robot so it doesn't continue to drive into the wall
                                holonomicDrive.stopMoving();

                                Sky_Claw.setPosition(0.3);
                                sleep(2000);
                                runtime.reset();
                                holonomicDrive.autoDrive(180, .55);
                                while (opModeIsActive() && runtime.seconds() < 5.0) {
                                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                                    telemetry.update();
                                }
                                holonomicDrive.stopMoving();
                            }
                            else if (recognition.getLabel() . equals("Stone") && pos != 3 )
                            {
                                pos++;
                                runtime.reset();
                                holonomicDrive.autoDrive(0, .55);
                                while (opModeIsActive() && runtime.seconds() < 1.0) {
                                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                                    telemetry.update();
                                }
                                holonomicDrive.stopMoving();
                                sleep(1000);

                            }




                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }
    }




    private void initVuforia() {



        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }





    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}


