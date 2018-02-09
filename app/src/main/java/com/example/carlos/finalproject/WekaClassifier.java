package com.example.carlos.finalproject;

/**
 * Created by RZ on 11/19/17.
 */

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N71b148d2106(i);
        return p;
    }
    static double N71b148d2106(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 123.808656) {
            p = WekaClassifier.N6bd6d141107(i);
        } else if (((Double) i[0]).doubleValue() > 123.808656) {
            p = WekaClassifier.N795100e5121(i);
        }
        return p;
    }
    static double N6bd6d141107(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 11.890617) {
            p = WekaClassifier.N7f9825ee108(i);
        } else if (((Double) i[0]).doubleValue() > 11.890617) {
            p = WekaClassifier.N67318705112(i);
        }
        return p;
    }
    static double N7f9825ee108(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 9.761689) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > 9.761689) {
            p = WekaClassifier.N7fb5db6a109(i);
        }
        return p;
    }
    static double N7fb5db6a109(Object []i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 3;
        } else if (((Double) i[22]).doubleValue() <= 0.422217) {
            p = WekaClassifier.N4b9fb96110(i);
        } else if (((Double) i[22]).doubleValue() > 0.422217) {
            p = 3;
        }
        return p;
    }
    static double N4b9fb96110(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 2;
        } else if (((Double) i[14]).doubleValue() <= 0.39887) {
            p = WekaClassifier.N59386e99111(i);
        } else if (((Double) i[14]).doubleValue() > 0.39887) {
            p = 3;
        }
        return p;
    }
    static double N59386e99111(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 0.690897) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 0.690897) {
            p = 2;
        }
        return p;
    }
    static double N67318705112(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 2;
        } else if (((Double) i[19]).doubleValue() <= 6.581393) {
            p = WekaClassifier.N368e7e02113(i);
        } else if (((Double) i[19]).doubleValue() > 6.581393) {
            p = WekaClassifier.N488e07ab120(i);
        }
        return p;
    }
    static double N368e7e02113(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if (((Double) i[9]).doubleValue() <= 1.862747) {
            p = WekaClassifier.N7da0560e114(i);
        } else if (((Double) i[9]).doubleValue() > 1.862747) {
            p = WekaClassifier.N36402722118(i);
        }
        return p;
    }
    static double N7da0560e114(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 65.076671) {
            p = WekaClassifier.N6776b385115(i);
        } else if (((Double) i[0]).doubleValue() > 65.076671) {
            p = WekaClassifier.N492f082f117(i);
        }
        return p;
    }
    static double N6776b385115(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 13.398214) {
            p = WekaClassifier.Nc1409c1116(i);
        } else if (((Double) i[0]).doubleValue() > 13.398214) {
            p = 2;
        }
        return p;
    }
    static double Nc1409c1116(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= 1.484132) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() > 1.484132) {
            p = 3;
        }
        return p;
    }
    static double N492f082f117(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 2;
        } else if (((Double) i[14]).doubleValue() <= 0.700614) {
            p = 2;
        } else if (((Double) i[14]).doubleValue() > 0.700614) {
            p = 3;
        }
        return p;
    }
    static double N36402722118(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 0;
        } else if (((Double) i[20]).doubleValue() <= 0.398648) {
            p = 0;
        } else if (((Double) i[20]).doubleValue() > 0.398648) {
            p = WekaClassifier.N64f840f4119(i);
        }
        return p;
    }
    static double N64f840f4119(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 0.763801) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() > 0.763801) {
            p = 2;
        }
        return p;
    }
    static double N488e07ab120(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 18.766059) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() > 18.766059) {
            p = 2;
        }
        return p;
    }
    static double N795100e5121(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 0;
        } else if (((Double) i[27]).doubleValue() <= 11.816397) {
            p = WekaClassifier.N4e9f41da122(i);
        } else if (((Double) i[27]).doubleValue() > 11.816397) {
            p = 1;
        }
        return p;
    }
    static double N4e9f41da122(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 162.63902) {
            p = WekaClassifier.N6846471d123(i);
        } else if (((Double) i[0]).doubleValue() > 162.63902) {
            p = WekaClassifier.N21eac225125(i);
        }
        return p;
    }
    static double N6846471d123(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 15.52907) {
            p = WekaClassifier.N504db6e1124(i);
        } else if (((Double) i[3]).doubleValue() > 15.52907) {
            p = 2;
        }
        return p;
    }
    static double N504db6e1124(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 26.503004) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 26.503004) {
            p = 0;
        }
        return p;
    }
    static double N21eac225125(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1386.933643) {
            p = WekaClassifier.N154fd653126(i);
        } else if (((Double) i[0]).doubleValue() > 1386.933643) {
            p = 1;
        }
        return p;
    }
    static double N154fd653126(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 0;
        } else if (((Double) i[10]).doubleValue() <= 23.269465) {
            p = WekaClassifier.N4feb1548127(i);
        } else if (((Double) i[10]).doubleValue() > 23.269465) {
            p = WekaClassifier.N68cc3c47138(i);
        }
        return p;
    }
    static double N4feb1548127(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 6.310584) {
            p = WekaClassifier.N722c58ea128(i);
        } else if (((Double) i[4]).doubleValue() > 6.310584) {
            p = WekaClassifier.N789c6f61136(i);
        }
        return p;
    }
    static double N722c58ea128(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 7.615872) {
            p = WekaClassifier.N5204760129(i);
        } else if (((Double) i[64]).doubleValue() > 7.615872) {
            p = WekaClassifier.N493802fb135(i);
        }
        return p;
    }
    static double N5204760129(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 2.813992) {
            p = WekaClassifier.N6e36e4d9130(i);
        } else if (((Double) i[6]).doubleValue() > 2.813992) {
            p = WekaClassifier.N6e0740d8133(i);
        }
        return p;
    }
    static double N6e36e4d9130(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 4.653984) {
            p = WekaClassifier.N7960fd02131(i);
        } else if (((Double) i[3]).doubleValue() > 4.653984) {
            p = 1;
        }
        return p;
    }
    static double N7960fd02131(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 0.624288) {
            p = WekaClassifier.N699b11cc132(i);
        } else if (((Double) i[20]).doubleValue() > 0.624288) {
            p = 0;
        }
        return p;
    }
    static double N699b11cc132(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() <= 0.37994) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() > 0.37994) {
            p = 1;
        }
        return p;
    }
    static double N6e0740d8133(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 0;
        } else if (((Double) i[27]).doubleValue() <= 1.136758) {
            p = 0;
        } else if (((Double) i[27]).doubleValue() > 1.136758) {
            p = WekaClassifier.N37ce61ed134(i);
        }
        return p;
    }
    static double N37ce61ed134(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 36.175132) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 36.175132) {
            p = 0;
        }
        return p;
    }
    static double N493802fb135(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 40.924051) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 40.924051) {
            p = 0;
        }
        return p;
    }
    static double N789c6f61136(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 0;
        } else if (((Double) i[20]).doubleValue() <= 1.2803) {
            p = WekaClassifier.N2c49f12a137(i);
        } else if (((Double) i[20]).doubleValue() > 1.2803) {
            p = 0;
        }
        return p;
    }
    static double N2c49f12a137(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 1.24194) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 1.24194) {
            p = 0;
        }
        return p;
    }
    static double N68cc3c47138(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 1;
        } else if (((Double) i[27]).doubleValue() <= 11.674702) {
            p = 1;
        } else if (((Double) i[27]).doubleValue() > 11.674702) {
            p = 0;
        }
        return p;
    }
}
