package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;



@Autonomous(name= "BlueAuto", group="Sky autonomous")
public class SkyProto extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor  FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor, IntakeLeftMotor, IntakeRightMotor, ScissorLiftMotor;
    private Servo left_hook, right_hook, IntakePulley;
    HolonomicDrive holonomicDrive;
    ScissorLift scissorLift;
    Intake_Systems intake_systems;
    gyro Gyro;
    double lStored = 0.9;
    double rStored = 0.3;
    double lActive = 0.5;
    double rActive = 0.1;
    double time = 5;
    double r_time = 7;
    int pos;
    boolean skyFound = false;
    boolean collect, deploy;

    //0 means skystone, 1 means yellow stone
    //-1 for debug, but we can keep it like this because if it works, it should change to either 0 or 255
    private static int valMid = -1;
    private static int valLeft = -1;
    private static int valRight = -1;

    private static float rectHeight = .6f/8f;
    private static float rectWidth = 1.5f/8f;

    private static float offsetX = 0f/8f;//changing this moves the three rects and the three circles left or right, range : (-2, 2) not inclusive
    private static float offsetY = 0f/8f;//changing this moves the three rects and circles up or down, range: (-4, 4) not inclusive

    private static float[] midPos = {4f/8f+offsetX, 4f/8f+offsetY};//0 = col, 1 = row
    private static float[] leftPos = {2f/8f+offsetX, 4f/8f+offsetY};
    private static float[] rightPos = {6f/8f+offsetX, 4f/8f+offsetY};
    //moves all rectangles right or left by amount. units are in ratio to monitor

    private final int rows = 640;
    private final int cols = 480;

    OpenCvCamera webcam;

    @Override
    public void runOpMode() throws InterruptedException {
        FrontRightMotor = hardwareMap.get(DcMotor.class, "front_right_drive");
        FrontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_drive");
        BackRightMotor = hardwareMap.get(DcMotor.class, "back_right_drive");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "back_left_drive");
        IntakeLeftMotor = hardwareMap.get(DcMotor.class, "left_intake");
        IntakeRightMotor = hardwareMap.get(DcMotor.class, "right_intake");
        ScissorLiftMotor =  hardwareMap.get(DcMotor.class, "scissor");
        IntakePulley = hardwareMap.servo.get("intake_pulley");
        left_hook = hardwareMap.servo.get("left_hook");
        right_hook = hardwareMap.servo.get("right_hook");

        holonomicDrive = new HolonomicDrive(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor);
        scissorLift = new ScissorLift(ScissorLiftMotor);
        intake_systems = new Intake_Systems(IntakeRightMotor, IntakeLeftMotor, IntakePulley);
        Gyro = new gyro();


        // Setting servos to the retracted position allowing them to move over the foundation lip
        left_hook .setPosition(lStored);
        right_hook.setPosition(rStored);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();//open camera
        webcam.setPipeline(new StageSwitchingPipeline());//different stages
        webcam.startStreaming(rows, cols, OpenCvCameraRotation.UPRIGHT);//display on RC
        //width, height
        //width = height in this case, because camera is in portrait mode.

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            telemetry.addData("Values", valLeft+"   "+valMid+"   "+valRight);
            telemetry.update();
            sleep(100);
/**          Testing if the skystone is on the left side of the robot
             If it is, we will strafe left so when we move forward, it will go into the intake
             If There is an issue with collecting it, then we can add a touch sensor to the inside of the intake which will let us detect
             when we have captured the skystone */

            if(valLeft == 0 && !skyFound){
                skyFound = true;
                pos = 1;
                time -= 0.5;
                r_time -= 0.5;
                runtime.reset();
                FrontRightMotor.setPower(-1.0);
                FrontLeftMotor.setPower(-1.0);
                BackRightMotor.setPower(-1.0);
                BackLeftMotor.setPower(-1.0);
                while (opModeIsActive() && runtime.seconds() < 0.5){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
            }
            else if(valRight == 0 && !skyFound){
                skyFound = true;
                pos = 3;
                time += 0.5;
                r_time += 0.5;
                runtime.reset();
                FrontRightMotor.setPower(1.0);
                FrontLeftMotor.setPower(1.0);
                BackRightMotor.setPower(1.0);
                BackLeftMotor.setPower(1.0);
                while (opModeIsActive() && runtime.seconds() < 0.5){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
            }
            else if(valMid == 0 && !skyFound) {
                skyFound = true;
                pos = 2;
            }



            else{
                telemetry.addData("Sorry!", "I don't see anything!");
                telemetry.update();
            }
            intake_systems.intake(collect, deploy);
            collect = true;
            runtime.reset();
            holonomicDrive.autoDrive(0,0.8);
            while (opModeIsActive() && runtime.seconds() < 3.0){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            Gyro.rotate(90,0.5);
            collect = false;
            runtime.reset();
            holonomicDrive.autoDrive(180,0.8);
            while (opModeIsActive() && runtime.seconds() < 3.0){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
        }

        /**
         *   Currently pointing towards foundation hopefully
         *   need to drive towards foundation w/ time variable,
         *   turn to the left
         *   drive forward
         *   grab foundation
         *   use gyro to rotate
         *   maybe drive forward & eject skystone;
         */
    }

    //detection pipeline
    static class StageSwitchingPipeline extends OpenCvPipeline
    {
        Mat yCbCrChan2Mat = new Mat();
        Mat thresholdMat = new Mat();
        Mat all = new Mat();
        List<MatOfPoint> contoursList = new ArrayList<>();

        enum Stage
        {//color difference. greyscale
            detection,//includes outlines
            THRESHOLD,//b&w
            RAW_IMAGE,//displays raw view
        }

        private Stage stageToRenderToViewport = Stage.detection;
        private Stage[] stages = Stage.values();

        @Override
        public void onViewportTapped()
        {
            /*
             * Note that this method is invoked from the UI thread
             * so whatever we do here, we must do quickly.
             */

            int currentStageNum = stageToRenderToViewport.ordinal();

            int nextStageNum = currentStageNum + 1;

            if(nextStageNum >= stages.length)
            {
                nextStageNum = 0;
            }

            stageToRenderToViewport = stages[nextStageNum];
        }

        @Override
        public Mat processFrame(Mat input)
        {
            contoursList.clear();
            /*
             * This pipeline finds the contours of yellow blobs such as the Gold Mineral
             * from the Rover Ruckus game.
             */

            //color diff cb.
            //lower cb = more blue = skystone = white
            //higher cb = less blue = yellow stone = grey
            Imgproc.cvtColor(input, yCbCrChan2Mat, Imgproc.COLOR_RGB2YCrCb);//converts rgb to ycrcb
            Core.extractChannel(yCbCrChan2Mat, yCbCrChan2Mat, 2);//takes cb difference and stores

            //b&w
            Imgproc.threshold(yCbCrChan2Mat, thresholdMat, 102, 255, Imgproc.THRESH_BINARY_INV);

            //outline/contour
            Imgproc.findContours(thresholdMat, contoursList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            yCbCrChan2Mat.copyTo(all);//copies mat object
            //Imgproc.drawContours(all, contoursList, -1, new Scalar(255, 0, 0), 3, 8);//draws blue contours


            //get values from frame
            double[] pixMid = thresholdMat.get((int)(input.rows()* midPos[1]), (int)(input.cols()* midPos[0]));//gets value at circle
            valMid = (int)pixMid[0];

            double[] pixLeft = thresholdMat.get((int)(input.rows()* leftPos[1]), (int)(input.cols()* leftPos[0]));//gets value at circle
            valLeft = (int)pixLeft[0];

            double[] pixRight = thresholdMat.get((int)(input.rows()* rightPos[1]), (int)(input.cols()* rightPos[0]));//gets value at circle
            valRight = (int)pixRight[0];

            //create three points
            Point pointMid = new Point((int)(input.cols()* midPos[0]), (int)(input.rows()* midPos[1]));
            Point pointLeft = new Point((int)(input.cols()* leftPos[0]), (int)(input.rows()* leftPos[1]));
            Point pointRight = new Point((int)(input.cols()* rightPos[0]), (int)(input.rows()* rightPos[1]));

            //draw circles on those points
            Imgproc.circle(all, pointMid,5, new Scalar( 255, 0, 0 ),1 );//draws circle
            Imgproc.circle(all, pointLeft,5, new Scalar( 255, 0, 0 ),1 );//draws circle
            Imgproc.circle(all, pointRight,5, new Scalar( 255, 0, 0 ),1 );//draws circle

            //draw 3 rectangles
            Imgproc.rectangle(//1-3
                    all,
                    new Point(
                            input.cols()*(leftPos[0]-rectWidth/2),
                            input.rows()*(leftPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(leftPos[0]+rectWidth/2),
                            input.rows()*(leftPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//3-5
                    all,
                    new Point(
                            input.cols()*(midPos[0]-rectWidth/2),
                            input.rows()*(midPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(midPos[0]+rectWidth/2),
                            input.rows()*(midPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//5-7
                    all,
                    new Point(
                            input.cols()*(rightPos[0]-rectWidth/2),
                            input.rows()*(rightPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(rightPos[0]+rectWidth/2),
                            input.rows()*(rightPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);

            switch (stageToRenderToViewport)
            {
                case THRESHOLD:
                {
                    return thresholdMat;
                }

                case detection:
                {
                    return all;
                }

                case RAW_IMAGE:
                {
                    return input;
                }

                default:
                {
                    return input;
                }
            }
        }

    }
}