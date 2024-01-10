package frc.robot.subsystems.intake;


import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.ma5951.utils.MAShuffleboard;
import com.ma5951.utils.subsystem.MotorSubsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.PortMap;
import com.revrobotics.*;
import com.revrobotics.Rev2mDistanceSensor.Port;

public class Intake extends SubsystemBase implements MotorSubsystem{
  private static Intake intake;
  private Rev2mDistanceSensor sensor;

  private CANSparkMax master;
  private CANSparkMax slave;


  private MAShuffleboard board;

  private boolean piceInIntake = false;

  private Intake() {
    master = new CANSparkMax(PortMap.Intake.masterID, MotorType.kBrushless);
    slave = new CANSparkMax(PortMap.Intake.slvaeID, MotorType.kBrushless);

    sensor = new Rev2mDistanceSensor(Port.kOnboard);
    board = new MAShuffleboard("Intake");

    master.setIdleMode(IdleMode.kBrake);
    slave.setIdleMode(IdleMode.kBrake);
    master.setInverted(false);
    slave.follow(master, true);

    sensor.setAutomaticMode(true);
  }

  public double getSensor() {
    if (sensor.isRangeValid()) {
      return sensor.getRange();
    } else {
      return 0;
    }
  }

  public double getIntakeCurrent() {
    return (master.getOutputCurrent() + slave.getOutputCurrent()) / 2;
  }

  public boolean isPiceIn() {
    return piceInIntake;
  }

  public void setPiceIn(boolean state) {
    piceInIntake = state;
  }

  @Override
  public boolean canMove() {
      return true;
  }

  @Override
  public void setVoltage(double voltage) {
      master.set(voltage / 12);
  }

  public static Intake getInstance() {
  if (intake == null) {
      intake = new Intake();  
    }
  return intake;
  }

  @Override
  public void periodic() {
    board.addBoolean("isPiceIn", piceInIntake);
    board.addNum("Avrage current", getIntakeCurrent());
    board.addNum("Sensor", getSensor());
  }

}
