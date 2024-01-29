// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ma5951.utils.Limelight;
import com.ma5951.utils.commands.MotorCommand;

import edu.wpi.first.math.geometry.Transform3d;

// import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import frc.robot.subsystems.swerve.SwerveDrivetrainSubsystem;
import frc.robot.automations.AMPScore;
import frc.robot.automations.IntakeAndRingCenter;
import frc.robot.automations.IntakeAutomation;
import frc.robot.automations.RunIntake;
import frc.robot.automations.ScoreWithoutAdjust;
import frc.robot.automations.Shoot;
import frc.robot.automations.SourceIntake;
import frc.robot.commands.Intake.IntakeCommand;
import frc.robot.commands.elevator.ResetElevator;
import frc.robot.commands.elevator.SetElevator;
import frc.robot.subsystems.LED.LED;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorConstants;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.shooter.ShooterConstants;

public class RobotContainer {
  private enum ScoringOptions {
    AMP,
    SPEAKER
  }

  public static ScoringOptions scoringOption = ScoringOptions.SPEAKER;

  public static final CommandPS5Controller
    driverController = new CommandPS5Controller(PortMap.Controllers.driveID);
  public static final CommandPS5Controller
    operatorController = new CommandPS5Controller(PortMap.Controllers.operatorID);

  public static final Limelight APRILTAGS_LIMELIGHT = new Limelight(
    "limelight-one", new Transform3d());
  
  private static boolean IsSpeaker() {
    return scoringOption == ScoringOptions.SPEAKER;
  }

  private void registerCommands() {
  }

  public RobotContainer() {
    registerCommands();
    configureBindings();
  }

  private void configureBindings() {
    driverController.R2().whileTrue(new InstantCommand(
      () -> SwerveDrivetrainSubsystem.getInstance().FactorVelocityTo(0.4)
    )).whileFalse(
      new InstantCommand(
      () -> SwerveDrivetrainSubsystem.getInstance().FactorVelocityTo(1)
    ));

    driverController.triangle().whileTrue(
      new InstantCommand(SwerveDrivetrainSubsystem.getInstance()::updateOffset)
    );

    // ---------------------------------------------------------------

    new CreateButton(driverController.povLeft(), new ResetElevator());

    driverController.povUp().whileTrue(new MotorCommand(
      Elevator.getInstance(), 0.3, 0
    )).whileFalse(
      new InstantCommand(() -> Elevator.getInstance().setSetPoint(Elevator.getInstance().getPosition()))
    );
    
    driverController.povDown().whileTrue(new MotorCommand(
      Elevator.getInstance(), -0.3, 0
    )).whileFalse(
      new InstantCommand(() -> Elevator.getInstance().setSetPoint(Elevator.getInstance().getPosition()))
    );


    // intake
    new CreateButton(driverController.R1(), 
      new IntakeAutomation(IntakeConstants.INTAKE_POWER));

    new CreateButton(driverController.circle(), new AMPScore());

    // shooting linked to the speaker 
    new CreateButton(driverController.L2(), new ScoreWithoutAdjust(
      () -> ShooterConstants.SPEAKER_UPPER_V, 
      () -> ShooterConstants.SPEAKER_LOWER_V,
        ElevatorConstants.DEFAULT_POSE));

    // shootiong linked to the podduim 
    new CreateButton(driverController.L1(), new Shoot(true));
    
    // // shooting or amp
    // new CreateButton(driverController.circle(), 
    //   new ConditionalCommand(new Shoot(false),
    //     new AMPScore(),
    //     RobotContainer::IsSpeaker
    //   )
    // );

    // climb
    // new CreateButton(operatorController.triangle(),
    //   new SetElevator(ElevatorConstants.climbPose),
    //   ElevatorConstants.closeClimbPose);

    // // sorce intake
    // new CreateButton(operatorController.square(), new SourceIntake());
    
    // // eject
    // new CreateButton(operatorController.cross(), new ScoreWithoutAdjust(
    //   () -> ShooterConstants.defaultV, () -> ShooterConstants.defaultV, ElevatorConstants.ejectPose
    // ));
    
    // // choosing btween apm score and amp score
    // operatorController.povUp().whileTrue(
    //   new InstantCommand(() -> scoringOption = ScoringOptions.SPEAKER)
    // );

    // operatorController.povDown().whileTrue(
    //   new InstantCommand(() -> scoringOption = ScoringOptions.AMP)
    // );

    // //--------------------LEDS-----------------------

    // operatorController.touchpad().whileTrue(
    //   new InstantCommand(() -> LED.getInstance().activateAmp())
    // );

    // operatorController.options().whileTrue(
    //   new InstantCommand(() -> LED.getInstance().activateCoalition())
    // );
  }
  public Command getAutonomousCommand() {
    return null;
  }
}
