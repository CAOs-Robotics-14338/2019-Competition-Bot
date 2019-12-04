package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
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



@Autonomous(name= "Blue Double Sky w/ Foundation", group="Blue")
public class Blue_Double_Sky_Foundation extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor  FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor, IntakeLeftMotor, IntakeRightMotor, ScissorLiftMotor;
    private Servo left_hook, right_hook, IntakePulley;
    HolonomicDrive holonomicDrive;
    ScissorLift scissorLift;
    Intake_Systems intake_systems;
    gyro Gyro;
    BNO055IMU               imu;
    Orientation lastAngles = new Orientation();
    double                  globalAngle, power = .30, correction;


    double lStored = 0;
    double rStored = 1;
    double lActive = 0.6;
    double rActive = 0.4;
    double time = 5;
    double intake_time = 2;
    double r_time = 7;
    int pos;
    boolean skyFound = false;
    boolean sky2Found = false;

    //0 means skystone, 1 means yellow stone
    //-1 for debug, but we can keep it like this because if it works, it should change to either 0 or 255
    private static int valMid = -1;
    private static int valLeft = -1;
    private static int valRight = -1;

    private static float rectHeight = .6f/8f;
    private static float rectWidth = 1.5f/8f;

    private static float offsetX = 0f/8f;//changing this moves the three rects and the three circles left or right, range : (-2, 2) not inclusive
    private static float offsetY = 1.5f/8f;//changing this moves the three rects and circles up or down, range: (-4, 4) not inclusive

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

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);


        holonomicDrive = new HolonomicDrive(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor);
        scissorLift = new ScissorLift(ScissorLiftMotor);
        intake_systems = new Intake_Systems(IntakeRightMotor, IntakeLeftMotor, IntakePulley);
        Gyro = new gyro(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor, imu);


        // Setting servos to the retracted position allowing them to move over the foundation lip
        left_hook .setPosition(lStored);
        right_hook.setPosition(rStored);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();//open camera
        webcam.setPipeline(new StageSwitchingPipeline());//different stages
        webcam.startStreaming(rows, cols, OpenCvCameraRotation.SIDEWAYS_RIGHT);//display on RC
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
                holonomicDrive.autoDrive(315,0.4);
                while (opModeIsActive() && runtime.seconds() < 0.5){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.addData("Position", pos);
                    telemetry.update();
                }

            }
            else if(valRight == 0 && !skyFound){
                skyFound = true;
                pos = 3;
                time += 0.5;
                r_time += 0.5;
                runtime.reset();
                holonomicDrive.autoDrive(45,0.4);
                while (opModeIsActive() && runtime.seconds() < 0.5){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.addData("Position", pos);

                    telemetry.update();
                }

            }
            else if(valMid == 0 && !skyFound) {
                skyFound = true;
                pos = 2;
                telemetry.addData("Position", pos);
            }
            else{
                telemetry.addData("Sorry!", "I don't see anything!");
                telemetry.update();
            }
            // After moving the robot infront of the skystone or displaying a message that we do not see one,
            // We will enable the intake and move forward for the amount of time defined by intake_time
            intake_systems.intake(true, false);
            runtime.reset();
            holonomicDrive.autoDrive(0,0.8);
            while (opModeIsActive() && runtime.seconds() < intake_time){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            sleep(500);
            // We will now have the skystone so we must drive back to the starting position and disable the collector
            intake_systems.intake(false, false);
            runtime.reset();
            holonomicDrive.autoDrive(180,0.8);
            while (opModeIsActive() && runtime.seconds() < intake_time){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            // We are now pointing towards our alliance side with the skystone, next we will turn
            // to the right 90 degrees so we are pointing towards the building zone
            Gyro.rotate(-90,0.5);
            sleep(1000);
            // Next we will want to drive towards the building zone for the amount of time defined by the variable "time"
            runtime.reset();
            holonomicDrive.autoDrive(180,0.8);
            while (opModeIsActive() && runtime.seconds() < time){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            intake_systems.intake(false, true);
            holonomicDrive.stopMoving();
            // We should now be parallel to the foundation so we now need to turn to the left and drive to the foundation
            Gyro.rotate(90,0.5);
            intake_systems.intake(false, false);
            sleep(1000);
            runtime.reset();
            holonomicDrive.autoDrive(0,0.8);
            while (opModeIsActive() && runtime.seconds() < 0.5){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            sleep(500);
            left_hook.setPosition(lActive);
            right_hook.setPosition(rActive);
            sleep(500);
            // Now we will want to backup with the foundation before turning to the left
            runtime.reset();
            holonomicDrive.autoDrive(0,0.8);
            while (opModeIsActive() && runtime.seconds() < 1.2){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            sleep(200);
            Gyro.rotate(90,0.5);
            sleep(1000);
            // We should now be perpendicular with alliance bridge, next we will drive the foundation into the building site
            runtime.reset();
            holonomicDrive.autoDrive(0,0.8);
            while (opModeIsActive() && runtime.seconds() < 0.6){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();
            sleep(200);
            // we will now retract the servos
            left_hook.setPosition(lStored);
            right_hook.setPosition(rStored);
            /**

             If the scissor lift can place the stone on the foundation, that code will go here
             */
            runtime.reset();
            // Now we have started deploying the skystone, next we will drive back to the quarry
            holonomicDrive.autoDrive(180,0.8);
            while (opModeIsActive() && runtime.seconds() < r_time){
                // Adding telemetry of the time elapsed
                telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }
            holonomicDrive.stopMoving();


            sleep(200);
            // Now we are back in the quarry so we will want to test which skystone we need to aim for.
            // Since the last skystone is along the wall, we will need to approach it at a different angle
            // than the other two, that is what the following if statements do.
            if(pos == 3 && !sky2Found){
                sky2Found = true;
                Gyro.rotate(-135,0.5);
                sleep(1000);
                intake_systems.intake(true, false);
                runtime.reset();
                holonomicDrive.autoDrive(0,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                sleep(500);
                runtime.reset();
                holonomicDrive.autoDrive(180,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time-1){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                Gyro.rotate(135,0.5);
                sleep(100);
                intake_systems.intake(false, false);

                runtime.reset();
                // Now we are driving towards the building zone with the skystone.
                holonomicDrive.autoDrive(0,1.0);
                while (opModeIsActive() && runtime.seconds() < r_time){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                intake_systems.intake(false, true);
                sleep(100);
                holonomicDrive.autoDrive(180,1.0);
                while (opModeIsActive() && runtime.seconds() < 1.5 ){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
            }
            // We should be with our intake facing the building site  but infront of the skystone
            else if(pos != 3 && !sky2Found){
                sky2Found = true;
                Gyro.rotate(-90, 0.5);
                sleep(1000);
                intake_systems.intake(true, false);
                // we should now be with our intake pointed at the second skystone.
                runtime.reset();
                holonomicDrive.autoDrive(0,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                sleep(200);
                runtime.reset();
                holonomicDrive.autoDrive(180,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                Gyro.rotate(90, 0.5);
                intake_systems.intake(false, false);
                runtime.reset();
                // Now we are driving towards the building zone with the skystone.
                holonomicDrive.autoDrive(0,1.0);
                while (opModeIsActive() && runtime.seconds() < r_time){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                intake_systems.intake(false, true);


                sleep(100);
                holonomicDrive.autoDrive(180,1.0);
                while (opModeIsActive() && runtime.seconds() < 1.5 ){
                    // Adding telemetry of the time elapsed
                    telemetry.addData("Path", "TIME: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                intake_systems.intake(false, false);



            }




        }


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