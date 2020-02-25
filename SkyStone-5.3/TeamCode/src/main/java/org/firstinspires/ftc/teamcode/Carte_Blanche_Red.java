package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
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

@Autonomous(name= "Carte Blanch Red", group="Red")
public class Carte_Blanche_Red extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor, IntakeLeftMotor, IntakeRightMotor, ScissorLiftMotorLeft, ScissorLiftMotorRight;
    private Servo IntakePulley, left_hook, right_hook, claw, capstone;
    private CRServo expansion;

    HolonomicDrive holonomicDrive;
    Intake_Systems intake_systems;
    BotServos botServos;
    ScissorLift scissorLift;
    gyro Gyro;
    BNO055IMU imu;
    ArmCollection armCollection;

    Orientation lastAngles = new Orientation();
    double                  globalAngle, power = .30, correction;


    double lStored = 0;
    double rStored = 1;
    double lActive = 0.6;
    double rActive = 0.4;

    double time = 2;
    double intake_time = 0.50;
    double wallToSS1 = 1.55;
    double wallToSS2 = 1.60;
    double wallToSS3 = 1.65;
    double SS1ToFoundation = 3.48;
    double SS2ToFoundation = 3.68;
    double SS3ToFoundation = 3.88;
    double pos1FND2SS2 = 4.00;
    double pos2FND2SS2 = 4.15;
    double pos3FND2SS2 = 4.30;
    double P1SS2ToFoundation = 4.1;
    double P2SS2ToFoundation = 4.25;
    double P3SS2ToFoundation = 4.40;
    double Foundation2Skybridge = 1.2;

    int pos;
    boolean skyFound = false;


    //0 means skystone, 1 means yellow stone
    //-1 for debug, but we can keep it like this because if it works, it should change to either 0 or 255
    private static int valMid = -1;
    private static int valLeft = -1;
    private static int valRight = -1;
    boolean posfound = false;

    private static float rectHeight = .6f/8f;
    private static float rectWidth = 1.5f/8f;

    private static float offsetX = -2.5f/8f;//changing this moves the three rects and the three circles left or right, range : (-2, 2) not inclusive
    private static float offsetY = 1.7f/8f;//changing this moves the three rects and circles up or down, range: (-4, 4) not inclusive
    private static float[] midPos = {3.5f/8f+offsetX, 4f/8f+offsetY};//0 = col, 1 = row
    private static float[] leftPos = {2.0f/8f+offsetX, 4f/8f+offsetY};
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
        ScissorLiftMotorLeft =  hardwareMap.get(DcMotor.class, "scissor_left");
        ScissorLiftMotorRight =  hardwareMap.get(DcMotor.class, "scissor_right");
        IntakePulley = hardwareMap.servo.get("intake_pulley");
        left_hook = hardwareMap.servo.get("left_hook");
        right_hook = hardwareMap.servo.get("right_hook");

        claw = hardwareMap.servo.get("claw");
        capstone = hardwareMap.servo.get("capstone");
        expansion = hardwareMap.get(CRServo.class,"expansion");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.loggingEnabled      = false;


        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);


        holonomicDrive = new HolonomicDrive(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor);
        scissorLift = new ScissorLift(ScissorLiftMotorLeft, ScissorLiftMotorRight);
        intake_systems = new Intake_Systems(IntakeRightMotor, IntakeLeftMotor, IntakePulley);
        Gyro = new gyro(FrontRightMotor, FrontLeftMotor, BackRightMotor, BackLeftMotor, imu);
        botServos = new BotServos(left_hook, right_hook);
        armCollection = new ArmCollection(claw, expansion, IntakePulley);


        // Setting servos to the retracted position
        left_hook .setPosition(lStored);
        right_hook.setPosition(rStored);
        IntakePulley.setPosition(0);
        armCollection.clawAuto(false);



        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam Red"), cameraMonitorViewId);
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
            if(valMid == 0 && !posfound){pos = 2; posfound = true;}
            else if(valRight == 0 && !posfound){pos = 1; posfound = true;}
            else{pos = 3; posfound = true;}
            armCollection.expandControl(-1);
            scissorLift.LiftControlAuto(0);


            if(pos == 1 && !skyFound){
                skyFound = true;

                sleep(200);
                // Driving from the wall to the first skystone @ position 1
                runtime.reset();
                holonomicDrive.autoDrive(330,0.9);
                while (opModeIsActive() && runtime.seconds() < wallToSS1){
                    telemetry.addLine("Driving to 1st Skystone at position 1");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                // Driving towards the Skystone with the claw ready
                runtime.reset();
                holonomicDrive.autoDrive(0,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time-1){
                    telemetry.addLine("Collecting the Skystone");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                sleep(1200);
                armCollection.clawAuto(true);
                sleep(250);

                // Reversing from collecting so we can drive under the alliance bridge
                runtime.reset();
                holonomicDrive.autoDrive(180,0.8);
                while (opModeIsActive() && runtime.seconds() < 0.3){
                    telemetry.addLine("Reversing to drive under the bridge");
                    telemetry.update();
                }

                holonomicDrive.stopMoving();
                armCollection.expandControl(-0.8);

                // Stopping collection wheels and rotating to point towards the building zone
                Gyro.rotate(-87, 0.35);
                armCollection.expandControl(0);
                sleep(150);

                // Driving towards the building site with skystone
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < SS1ToFoundation){
                    telemetry.addLine("Driving to the building site");
                    telemetry.update();

                }
                holonomicDrive.stopMoving();

                scissorLift.LiftControlAuto(0.7);

                // Turning towards the foundation & driving into it
                Gyro.rotate(85,0.5);

                sleep(150);



                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.75){
                    telemetry.addLine("Moving Foundation");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                botServos.auto(true);
                scissorLift.LiftControlAuto(0);
                sleep(500);



                // Reverse with foundation, rotate, and push into wall
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 1.55){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-88,0.75);

                sleep(150);
                armCollection.clawAuto(false);
                botServos.auto(false);
                sleep(400);

                runtime.reset();
                holonomicDrive.autoDrive(175,0.90);
                while (opModeIsActive() && runtime.seconds() < pos1FND2SS2){
                    if(runtime.seconds() > 0.5 && runtime.seconds() < 2){
                        scissorLift.LiftControlAuto(-0.4);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();

                Gyro.rotate(83,0.4);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.15){}
                holonomicDrive.stopMoving();
                armCollection.clawAuto(true);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.35){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-83, 0.4);
                sleep(150);

                runtime.reset();
                holonomicDrive.autoDrive(0,0.95);
                while (opModeIsActive() && runtime.seconds() < P1SS2ToFoundation){
                    if(runtime.seconds() > 2.9 ){
                        scissorLift.LiftControlAuto(0.7);
                    }
                }
                holonomicDrive.stopMoving();
                armCollection.clawAuto(false);

                runtime.reset();
                holonomicDrive.autoDrive(180,0.95);
                while (opModeIsActive() && runtime.seconds() < Foundation2Skybridge){
                    if(runtime.seconds() > 0.5  && runtime.seconds() < Foundation2Skybridge){
                        scissorLift.LiftControlAuto(-0.5);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();
                scissorLift.LiftControlAuto(0);

                stop();

            }
            else if(pos == 2 && !skyFound){

                skyFound = true;

                sleep(200);
                // Driving from the wall to the first skystone @ position 2
                runtime.reset();
                holonomicDrive.autoDrive(320,0.9);
                while (opModeIsActive() && runtime.seconds() < wallToSS2){
                    telemetry.addLine("Driving to 1st Skystone at position 2");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                // Driving towards the Skystone with the claw ready
                runtime.reset();
                holonomicDrive.autoDrive(0,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time-1){
                    telemetry.addLine("Collecting the Skystone");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                sleep(1200);
                armCollection.clawAuto(true);
                sleep(250);

                // Reversing from collecting so we can drive under the alliance bridge
                runtime.reset();
                holonomicDrive.autoDrive(180,0.8);
                while (opModeIsActive() && runtime.seconds() < 0.3){
                    telemetry.addLine("Reversing to drive under the bridge");
                    telemetry.update();
                }

                holonomicDrive.stopMoving();
                armCollection.expandControl(-0.7);

                // Stopping collection wheels and rotating to point towards the building zone
                Gyro.rotate(-87, 0.35);
                armCollection.expandControl(0);
                sleep(150);

                // Driving towards the building site with skystone
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < SS2ToFoundation){
                    telemetry.addLine("Driving to the building site");
                    telemetry.update();

                }
                holonomicDrive.stopMoving();

                scissorLift.LiftControlAuto(0.7);

                // Turning towards the foundation & driving into it
                Gyro.rotate(85,0.5);

                sleep(150);



                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.75){
                    telemetry.addLine("Moving Foundation");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                botServos.auto(true);
                scissorLift.LiftControlAuto(0);
                sleep(500);



                // Reverse with foundation, rotate, and push into wall
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 1.55){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-88,0.75);

                sleep(150);
                armCollection.clawAuto(false);
                botServos.auto(false);
                sleep(400);

                runtime.reset();
                holonomicDrive.autoDrive(175,0.90);
                while (opModeIsActive() && runtime.seconds() < pos2FND2SS2){
                    if(runtime.seconds() > 0.5 && runtime.seconds() < 2){
                        scissorLift.LiftControlAuto(-0.4);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();

                Gyro.rotate(83,0.4);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.15){}
                holonomicDrive.stopMoving();
                armCollection.clawAuto(true);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.35){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-83, 0.4);
                sleep(150);

                runtime.reset();
                holonomicDrive.autoDrive(0,0.95);
                while (opModeIsActive() && runtime.seconds() < P2SS2ToFoundation){
                    if(runtime.seconds() > 2.9 ){
                        scissorLift.LiftControlAuto(0.7);
                    }
                }
                holonomicDrive.stopMoving();
                armCollection.clawAuto(false);

                runtime.reset();
                holonomicDrive.autoDrive(180,0.95);
                while (opModeIsActive() && runtime.seconds() < Foundation2Skybridge){
                    if(runtime.seconds() > 0.5  && runtime.seconds() < Foundation2Skybridge){
                        scissorLift.LiftControlAuto(-0.5);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();
                scissorLift.LiftControlAuto(0);

                stop();

            }
            else if(pos == 3 && !skyFound) {
                skyFound = true;

                sleep(200);
                // Driving from the wall to the first skystone @ position 1
                runtime.reset();
                holonomicDrive.autoDrive(310,0.9);
                while (opModeIsActive() && runtime.seconds() < wallToSS3){
                    telemetry.addLine("Driving to 1st Skystone at position 3");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                // Driving towards the Skystone with the claw ready
                runtime.reset();
                holonomicDrive.autoDrive(0,0.8);
                while (opModeIsActive() && runtime.seconds() < intake_time-1){
                    telemetry.addLine("Collecting the Skystone");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                sleep(1200);
                armCollection.clawAuto(true);
                sleep(250);

                // Reversing from collecting so we can drive under the alliance bridge
                runtime.reset();
                holonomicDrive.autoDrive(180,0.8);
                while (opModeIsActive() && runtime.seconds() < 0.3){
                    telemetry.addLine("Reversing to drive under the bridge");
                    telemetry.update();
                }

                holonomicDrive.stopMoving();
                armCollection.expandControl(-0.7);

                // Stopping collection wheels and rotating to point towards the building zone
                Gyro.rotate(-87, 0.35);
                armCollection.expandControl(0);
                sleep(150);

                // Driving towards the building site with skystone
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < SS3ToFoundation){
                    telemetry.addLine("Driving to the building site");
                    telemetry.update();

                }
                holonomicDrive.stopMoving();

                scissorLift.LiftControlAuto(0.7);

                // Turning towards the foundation & driving into it
                Gyro.rotate(85,0.5);

                sleep(150);



                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.75){
                    telemetry.addLine("Moving Foundation");
                    telemetry.update();
                }
                holonomicDrive.stopMoving();
                botServos.auto(true);
                scissorLift.LiftControlAuto(0);
                sleep(500);



                // Reverse with foundation, rotate, and push into wall
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 1.55){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-88,0.75);

                sleep(150);
                armCollection.clawAuto(false);
                botServos.auto(false);
                sleep(400);

                runtime.reset();
                holonomicDrive.autoDrive(175,0.90);
                while (opModeIsActive() && runtime.seconds() < pos3FND2SS2){
                    if(runtime.seconds() > 0.5 && runtime.seconds() < 2){
                        scissorLift.LiftControlAuto(-0.4);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();

                Gyro.rotate(83,0.4);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(0,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.15){}
                holonomicDrive.stopMoving();
                armCollection.clawAuto(true);
                sleep(100);
                runtime.reset();
                holonomicDrive.autoDrive(180,0.90);
                while (opModeIsActive() && runtime.seconds() < 0.35){}
                holonomicDrive.stopMoving();


                Gyro.rotate(-83, 0.4);
                sleep(150);

                runtime.reset();
                holonomicDrive.autoDrive(0,0.95);
                while (opModeIsActive() && runtime.seconds() < P3SS2ToFoundation){
                    if(runtime.seconds() > 2.9 ){
                        scissorLift.LiftControlAuto(0.7);
                    }
                }
                holonomicDrive.stopMoving();
                armCollection.clawAuto(false);

                runtime.reset();
                holonomicDrive.autoDrive(180,0.95);
                while (opModeIsActive() && runtime.seconds() < Foundation2Skybridge){
                    if(runtime.seconds() > 0.5  && runtime.seconds() < Foundation2Skybridge){
                        scissorLift.LiftControlAuto(-0.5);
                    }
                    else{
                        scissorLift.LiftControlAuto(0);
                    }
                }
                holonomicDrive.stopMoving();
                scissorLift.LiftControlAuto(0);

                stop();



            }
            else{
                telemetry.addData("Sorry!", "I don't see anything!");
                telemetry.update();
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