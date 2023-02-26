package ec.app.DMEtutorial;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import ec.gp.*;

@SuppressWarnings("serial")
public class MultiData extends GPData
{
    private MultiDataType data_type	   = MultiDataType.Double;
    private double[]      double_data  = new double[3];
    private BufferedImage texture_data = null;

    public MultiData(){};
    public MultiData(double d)        { this.SetTo(d); }
    public MultiData(boolean b)       { this.SetTo(b); }
    public MultiData(double[] v)      { this.SetTo(v); }
    public MultiData(BufferedImage t) { this.SetTo(t); }

    public static enum MultiDataType
    {
        Double,
        Boolean,
        Vector,
        Texture
    }

    @Override
    public void copyTo(final GPData gpd)
    {
        MultiData to = ((MultiData)gpd);

        to.double_data = new double[this.double_data.length];
        System.arraycopy(this.double_data, 0, to.double_data, 0, to.double_data.length);

        to.texture_data = deepCopy(texture_data);
    }

    @Override
    public Object clone()
    {
        MultiData other = (MultiData)(super.clone());

        other.double_data  = (double[])(double_data.clone());
        other.texture_data = deepCopy(texture_data) ;

        return other;
    }

    @Override
    public String toString()
    {
        String outstr = "";
        switch(data_type)
        {
            case Double:  outstr = "D:"+String.valueOf(GetD()); break;
            case Boolean: outstr = "B:"+String.valueOf(GetB()); break;
            case Vector:  outstr = "S:"+VectorNotation(GetS()); break;
            case Texture: outstr = "T:TextureData"; break;
        }
        return outstr;
    }

    static BufferedImage deepCopy(BufferedImage bi)
    {
        if(bi == null) return null;
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    static String VectorNotation(double[] v)
    {
        String s = "[";
        for(int i = 0; i < v.length; i++)
            s += String.valueOf(v[i]) + ( i==v.length-1 ? "]" : "," );
        return s;
    }

    public MultiDataType GetDataType() { return data_type; }
    public void SetDataType(MultiDataType t){ data_type = t; }

    public BufferedImage GetT() { return texture_data;          }
    public double        GetD() { return double_data[0];        }
    public double[]      GetS() { return double_data;           }
    public boolean       GetB() { return double_data[0] != 0.0; }

    public void SetTo(BufferedImage v) { data_type=MultiDataType.Texture; texture_data = v;                }
    public void SetTo(double        v) { data_type=MultiDataType.Double;  double_data[0] = v;              }
    public void SetTo(double[]      v) { data_type=MultiDataType.Vector;  double_data = v;                 }
    public void SetTo(boolean       v) { data_type=MultiDataType.Boolean; double_data[0] = v ? 1.0 : 0.0 ; }

}
