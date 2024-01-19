package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.chalet.Roof;
import ca.ulaval.glo2004.util.math.Imperial;

public class RoofDTO {
    public Orientation orientation;
    public float angle;
    public Imperial thickness;

    public RoofDTO(Roof roof)
    {
       this.orientation = roof.getOrientation();
       this.angle = roof.getAngle();
       this.thickness = (Imperial) roof.getThickness().clone();
    }
}
