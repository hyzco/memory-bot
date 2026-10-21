/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.deemeplus.ofus
 *  com.deemeplus.utils.DivContainer
 *  eu.darkbot.api.game.other.Gui
 *  eu.darkbot.api.managers.GameScreenAPI
 *  skidmod.Factory
 */
package com.deemeplus.modules.astral;

import com.deemeplus.ofus;
import com.deemeplus.utils.DivContainer;
import eu.darkbot.api.game.other.Gui;
import eu.darkbot.api.managers.GameScreenAPI;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import skidmod.Factory;

public class AstralMediator {
    private Gui astralGui;
    private Gui astralShipSelectionGui;
    private Random rnd;
    private static int QMHucVC4Eb = 0;
    private transient int CQMg54U3ie = 466025819;
    private static String[] nothing_to_see_here = new String[18];

    public AstralMediator(GameScreenAPI gameScreenAPI, int n) {
        int n2 = 0x55D9CF8E ^ 0x59248E16;
        AstralMediator astralMediator = this;
        {
            switch (Factory.jarwsmbhjwbijwmk((int)n2)) {
                case 546054434: {
                    continue block5;
                }
                case 153789419: {
                    n2 = 0x508E11E1 ^ n2;
                }
                case 941212469: {
                    Random random;
                    Gui gui;
                    Gui gui2;
                    n2 = 0x6F3FCE0A ^ (0x381ACED7 ^ (0x7D294242 ^ QMHucVC4Eb ^ n));
                    this.CQMg54U3ie = 0x1BC6FD5B ^ QMHucVC4Eb;
                    n2 = AstralMediator.rwurrwdaddxgwsuy(n2, 1419405375);
                    n2 = 0x3F6EE720 ^ n2;
                    AstralMediator astralMediator2 = this;
                    String string = "rogue_lite";
                    astralMediator2.astralGui = gui2 = gameScreenAPI.getGui(string);
                    n2 = 0x13B1867B ^ n2;
                    AstralMediator astralMediator3 = this;
                    String string2 = "rogue_lite_selection";
                    astralMediator3.astralShipSelectionGui = gui = gameScreenAPI.getGui(string2);
                    n2 = 0x5B644D21 ^ n2;
                    AstralMediator astralMediator4 = this;
                    astralMediator4.rnd = random = new Random();
                    n2 = 0x1B1308DC ^ n2;
                    return;
                }
            }
        }
        throw new RuntimeException();
    }

    /*
     * Exception decompiling
     */
    protected boolean isAstralGuiVisible(int var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [26[CATCHBLOCK]], but top level block is 9[CASE]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    protected void setAstralGuiVisible(boolean var1_1, int var2_2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: First case is not immediately after switch.
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:442)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:94)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    protected boolean isAstralShipSelectionGuiVisible(int var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Unable to fully structure code
     */
    protected void setShipSelectionGuiVisible(boolean var1_1, int var2_2) {
        block39: {
            block41: {
                block37: {
                    block40: {
                        block36: {
                            block38: {
                                var12_3 = 1075771201 ^ (2068034859 ^ (this.CQMg54U3ie ^ var2_2));
                                if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 40633907) break block37;
                                var12_3 = 508382973 ^ var12_3;
                                var3_4 = this;
                                var4_5 = var3_4.astralShipSelectionGui;
                                if (var4_5 == null) break block38;
                                if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 104982783) break block39;
                                var12_3 = 1913781129 ^ var12_3;
                                var5_6 = this;
                                var6_7 = var5_6.astralShipSelectionGui;
                                var7_8 = var6_7.isVisible();
                                if (var7_8 == var1_1) break block40;
                                if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 155192632) {
                                    var12_3 = 1325205917 ^ var12_3;
                                    var8_9 = this;
                                    var9_10 = var8_9.astralShipSelectionGui;
                                    var10_11 = var9_10.setVisible(var1_1);
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 128524229) {
                                        var12_3 = 1781681423 ^ var12_3;
lbl20:
                                        // 4 sources

                                        return;
                                    }
                                }
                                break block37;
                            }
                            block16: while (true) {
                                switch (Factory.jarwsmbhjwbijwmk((int)var12_3)) {
                                    case 1415005493: {
                                        continue block16;
                                    }
                                    case 104982783: {
                                        var12_3 = 317784172 ^ var12_3;
                                    }
                                    case 1330270921: {
                                        if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 168348212) break block16;
                                        if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 168348212) break block36;
                                    }
lbl34:
                                    // 3 sources

                                    default: {
                                        do {
                                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 168348212) ** GOTO lbl34
                                        } while (Factory.jarwsmbhjwbijwmk((int)var12_3) != (487121348 ^ var12_3));
                                        AstralMediator.QMHucVC4Eb = 464181516 ^ var12_3;
                                        throw new IllegalAccessException("double");
                                    }
                                }
                                break;
                            }
                            var12_3 = AstralMediator.rwurrwdaddxgwsuy(var12_3, 1143979895);
                            ** GOTO lbl20
                        }
                        var12_3 = 1244220237 ^ var12_3;
                        break block37;
                    }
                    block19: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var12_3)) {
                            case 1208664743: {
                                continue block19;
                            }
                            case 155192632: {
                                var12_3 = 971101450 ^ var12_3;
                                break block19;
                            }
lbl53:
                            // 2 sources

                            default: {
                                do {
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 236057866) ** GOTO lbl53
                                } while (Factory.jarwsmbhjwbijwmk((int)var12_3) != (1976454226 ^ var12_3));
                                AstralMediator.QMHucVC4Eb = 776603929 ^ var12_3;
                                throw new IOException("double");
                            }
                            case 1832514577: 
                        }
                        break;
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 108715069) break block41;
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 108715069) {
                        var12_3 = AstralMediator.rwurrwdaddxgwsuy(var12_3, 321414562);
                    }
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 108715069 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (1950562852 ^ var12_3)) {
                        continue;
                    }
                    AstralMediator.QMHucVC4Eb = 1790864125 ^ var12_3;
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 155192632 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (566423476 ^ var12_3)) continue;
                    this.CQMg54U3ie = 436492724 ^ var12_3;
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 40633907 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (701809060 ^ var12_3)) continue;
                    AstralMediator.QMHucVC4Eb = 1741079503 ^ var12_3;
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 128524229 && Factory.jarwsmbhjwbijwmk((int)var12_3) == (506833280 ^ var12_3)) break;
                }
                AstralMediator.QMHucVC4Eb = 2012803211 ^ var12_3;
                throw new RuntimeException();
            }
            block23: while (true) {
                switch (Factory.jarwsmbhjwbijwmk((int)var12_3)) {
                    case 290803411: {
                        continue block23;
                    }
                    case 1083458745: {
                        ** GOTO lbl20
                    }
                    case 108715069: {
                        var12_3 = 489591192 ^ var12_3;
                        ** continue;
                    }
                }
                break;
            }
        }
lbl87:
        // 2 sources

        do {
            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 104982783) ** GOTO lbl87
        } while (Factory.jarwsmbhjwbijwmk((int)var12_3) != (1102649545 ^ var12_3));
        AstralMediator.QMHucVC4Eb = 255483280 ^ var12_3;
        throw new IllegalAccessException("double");
    }

    /*
     * Unable to fully structure code
     */
    protected void chooseItem(int var1_1, boolean var2_2, int var3_3) {
        var8_4 = 1844929869 ^ (321470858 ^ (this.CQMg54U3ie ^ var3_3));
        if (Factory.jarwsmbhjwbijwmk((int)var8_4) == 175425003) {
            var8_4 = 2119654492 ^ var8_4;
            var4_5 = this;
            var5_6 = var4_5.getItemToSelect(var1_1, 167280067);
            if (Factory.jarwsmbhjwbijwmk((int)var8_4) == 246419769) {
                var8_4 = 66665571 ^ var8_4;
                var6_7 = this;
                var6_7.clickInItem(var5_6, var2_2, 535643916);
                if (Factory.jarwsmbhjwbijwmk((int)var8_4) == 17503816) {
                    var8_4 = 186382671 ^ var8_4;
                    return;
                } else {
                    ** GOTO lbl-1000
                }
            }
        } else lbl-1000:
        // 3 sources

        {
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var8_4) != 17503816 || Factory.jarwsmbhjwbijwmk((int)var8_4) != (2006604028 ^ var8_4)) {
                    continue;
                }
                this.CQMg54U3ie = 555813697 ^ var8_4;
                if (Factory.jarwsmbhjwbijwmk((int)var8_4) != 241876021 || Factory.jarwsmbhjwbijwmk((int)var8_4) != (1158947714 ^ var8_4)) continue;
                AstralMediator.QMHucVC4Eb = 1449506965 ^ var8_4;
                if (Factory.jarwsmbhjwbijwmk((int)var8_4) == 175425003 && Factory.jarwsmbhjwbijwmk((int)var8_4) == (1757946641 ^ var8_4)) break;
            }
            AstralMediator.QMHucVC4Eb = 1759812310 ^ var8_4;
            throw new RuntimeException();
        }
        while (Factory.jarwsmbhjwbijwmk((int)var8_4) != 246419769 || Factory.jarwsmbhjwbijwmk((int)var8_4) != (156611102 ^ var8_4)) {
        }
        this.CQMg54U3ie = 990649084 ^ var8_4;
        throw new IllegalAccessException("double");
    }

    protected void chooseShip(int n, boolean bl, int n2) {
        int n3 = 0x69568B88 ^ (0x1B256B9D ^ (this.CQMg54U3ie ^ n2));
        if (Factory.jarwsmbhjwbijwmk((int)n3) == 10577155) {
            n3 = 0x4706C41E ^ n3;
            AstralMediator astralMediator = this;
            DivContainer divContainer = astralMediator.getShipToSelect(n, 724020333);
            if (Factory.jarwsmbhjwbijwmk((int)n3) == 182152765) {
                n3 = 0x17A40F2C ^ n3;
                AstralMediator astralMediator2 = this;
                astralMediator2.clickInItemAstralSelection(divContainer, bl, 942350349);
                if (Factory.jarwsmbhjwbijwmk((int)n3) == 186419167) {
                    n3 = 0x39C63D66 ^ n3;
                    return;
                }
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 186419167 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x4D97B1C7 ^ n3)) {
                continue;
            }
            QMHucVC4Eb = 0x7B1DA435 ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 10577155 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x501FFEF ^ n3)) continue;
            this.CQMg54U3ie = 0x5C1D49AE ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) == 182152765 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x10574345 ^ n3)) break;
        }
        QMHucVC4Eb = 0x71B37FC6 ^ n3;
        throw new IllegalAccessException();
    }

    protected void clickInItem(DivContainer divContainer, boolean bl, int n) {
        int n2 = 0x652C5A89 ^ (0x5AFDC10E ^ (this.CQMg54U3ie ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 237008206) {
            n2 = 0x389F26DB ^ n2;
            AstralMediator astralMediator = this;
            AstralMediator astralMediator2 = this;
            Gui gui = astralMediator2.astralGui;
            astralMediator.clickInGui(gui, divContainer, bl, 1290583414);
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 261500138) {
                n2 = 0x1BE7E097 ^ n2;
                return;
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 53603453 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x789F211B ^ n2)) {
                continue;
            }
            this.CQMg54U3ie = 0x25BA686B ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 261500138 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x565E53BD ^ n2)) continue;
            this.CQMg54U3ie = 0x67544832 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 237008206 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x136F1CF5 ^ n2)) break;
        }
        QMHucVC4Eb = 0x475A217E ^ n2;
        throw new RuntimeException();
    }

    protected void clickInItemAstralSelection(DivContainer divContainer, boolean bl, int n) {
        int n2 = 0x5C65D5FF ^ (0x1CFB2E95 ^ (this.CQMg54U3ie ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 162114725) {
            n2 = 0x470A8973 ^ n2;
            AstralMediator astralMediator = this;
            AstralMediator astralMediator2 = this;
            Gui gui = astralMediator2.astralShipSelectionGui;
            astralMediator.clickInGui(gui, divContainer, bl, 1290583414);
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 187528930) {
                n2 = 0x4C3732B ^ n2;
                return;
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 187528930 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x38D3E437 ^ n2)) {
                continue;
            }
            QMHucVC4Eb = 0x7E9BD45B ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 48999777 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x657E3B69 ^ n2)) continue;
            QMHucVC4Eb = 0x7129C911 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 162114725 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x309D8834 ^ n2)) break;
        }
        QMHucVC4Eb = 0x45F1A872 ^ n2;
        throw new IllegalAccessException();
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Handled duff style switch with additional control
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void clickInGui(Gui var1_1, DivContainer var2_2, boolean var3_3, int var4_4) {
        block25: {
            block26: {
                block29: {
                    block30: {
                        block27: {
                            block31: {
                                block28: {
                                    var18_5 = 1814812364 ^ (1377643040 ^ (this.CQMg54U3ie ^ var4_4));
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 247235992) break block27;
                                    if (var3_3 == (660725185 ^ (var18_5 = 1436020961 ^ var18_5))) break block28;
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 206420676) break block25;
                                    var18_5 = 376790085 ^ var18_5;
                                    var5_6 = var2_2.getRandomX(1235272712);
                                    var7_7 = (int)var5_6;
                                    var11_8 = var2_2.getRandomY(646875175);
                                    var13_9 = (int)var11_8;
                                    var1_1.click(var7_7, var13_9);
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 253201403) break block29;
                                    var18_5 = AstralMediator.rwurrwdaddxgwsuy(var18_5, 1776807279);
                                    try {
                                        if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 207092724) throw new RuntimeException();
                                        throw null;
                                    }
                                    catch (RuntimeException v0) {
                                        switch (Factory.efuojskrxnitvcvt((int)var18_5)) {
                                            case 666446665: {
                                                var18_5 = AstralMediator.rwurrwdaddxgwsuy(var18_5, 1147535941);
                                                break;
                                            }
                                            default: {
                                                throw new IllegalAccessException("Error in hash");
                                            }
                                            case -946116774: {
                                                var18_5 = 945966023 ^ var18_5;
                                            }
                                        }
                                        if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 186211188) break block25;
                                    }
                                    break block30;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(var18_5 = AstralMediator.rwurrwdaddxgwsuy(var18_5, 1532425549))) == 11182776) break block31;
                                if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 11182776) {
                                    var18_5 = 1733099304 ^ var18_5;
                                }
                                break block25;
                            }
                            var18_5 = AstralMediator.rwurrwdaddxgwsuy(var18_5, 743705144);
                            ** GOTO lbl54
                        }
                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 207092724 || Factory.jarwsmbhjwbijwmk((int)var18_5) != (1850781650 ^ var18_5)) {
                                continue;
                            }
                            AstralMediator.QMHucVC4Eb = 762940552 ^ var18_5;
                            if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 247235992 && Factory.jarwsmbhjwbijwmk((int)var18_5) == (157479629 ^ var18_5)) break;
                        }
                        this.CQMg54U3ie = 2146043923 ^ var18_5;
                        throw new IOException("double");
                    }
                    block13: while (true) {
                        cfr_temp_0 = 0;
lbl47:
                        // 2 sources

                        while (true) {
                            switch (cfr_temp_0 == 0 ? Factory.jarwsmbhjwbijwmk((int)var18_5) : cfr_temp_0) {
                                case 689374310: {
                                    continue block13;
                                }
                                case 186211188: {
                                    var18_5 = 1351960381 ^ var18_5;
                                    ** GOTO lbl63
                                }
lbl54:
                                // 1 sources

                                var8_10 = var2_2.getCenterX(1044616711);
                                var10_11 = (int)var8_10;
                                var14_12 = var2_2.getCenterY(325251713);
                                var16_13 = (int)var14_12;
                                var1_1.click(var10_11, var16_13);
                                if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 197149535) {
                                    cfr_temp_0 = 104964854;
                                    var18_5 = 1617380005 ^ var18_5;
                                }
                                break block26;
lbl63:
                                // 2 sources

                                case 104964854: {
                                    return;
                                }
                            }
                            break;
                        }
                        break;
                    }
                    break block29;
                    ** while (true)
                }
lbl68:
                // 2 sources

                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 253201403) ** GOTO lbl68
                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) != (309348521 ^ var18_5)) continue;
                    this.CQMg54U3ie = 1933250978 ^ var18_5;
                    if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 218647076 && Factory.jarwsmbhjwbijwmk((int)var18_5) == (2139037735 ^ var18_5)) break;
                }
                AstralMediator.QMHucVC4Eb = 202811175 ^ var18_5;
                throw new RuntimeException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 197149535 || Factory.jarwsmbhjwbijwmk((int)var18_5) != (1593587718 ^ var18_5)) {
                    continue;
                }
                AstralMediator.QMHucVC4Eb = 1363165694 ^ var18_5;
                if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 127859852 && Factory.jarwsmbhjwbijwmk((int)var18_5) == (729582934 ^ var18_5)) break;
            }
            AstralMediator.QMHucVC4Eb = 1707146937 ^ var18_5;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 88074934 || Factory.jarwsmbhjwbijwmk((int)var18_5) != (991140837 ^ var18_5)) {
                continue;
            }
            AstralMediator.QMHucVC4Eb = 9005216 ^ var18_5;
            if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 11182776 || Factory.jarwsmbhjwbijwmk((int)var18_5) != (503750267 ^ var18_5)) continue;
            AstralMediator.QMHucVC4Eb = 2008601410 ^ var18_5;
            if (Factory.jarwsmbhjwbijwmk((int)var18_5) != 206420676 || Factory.jarwsmbhjwbijwmk((int)var18_5) != (1722662425 ^ var18_5)) continue;
            this.CQMg54U3ie = 2094318789 ^ var18_5;
            if (Factory.jarwsmbhjwbijwmk((int)var18_5) == 186211188 && Factory.jarwsmbhjwbijwmk((int)var18_5) == (505141505 ^ var18_5)) break;
        }
        this.CQMg54U3ie = 601872999 ^ var18_5;
        throw new RuntimeException();
    }

    /*
     * Unable to fully structure code
     */
    protected void clickAcceptButtonInShipSelection(int var1_1) {
        var39_2 = 2005124023 ^ (615658042 ^ (this.CQMg54U3ie ^ var1_1));
        if (Factory.jarwsmbhjwbijwmk((int)var39_2) == 60189520) {
            var39_2 = 2048375485 ^ var39_2;
            var2_3 = this;
            var6_4 = var2_3.astralShipSelectionGui;
            var7_5 = var6_4.getWidth();
            var3_6 = 20.0;
            var9_7 = var7_5 - var3_6;
            var25_8 = this;
            var26_9 = var25_8.rnd;
            var5_10 = (byte)(648485409 ^ var39_2);
            var27_11 = var26_9.nextInt(var5_10);
            var28_12 = var27_11;
            var11_13 = var9_7 - var28_12;
            var13_14 = (int)var11_13;
            if (Factory.jarwsmbhjwbijwmk((int)var39_2) == 182707266) {
                var39_2 = 295999024 ^ var39_2;
                var14_15 = this;
                var15_16 = var14_15.astralShipSelectionGui;
                var16_17 = var15_16.getHeight();
                var30_18 = 20.0;
                var18_19 = var16_17 - var30_18;
                var32_20 = this;
                var33_21 = var32_20.rnd;
                var37_22 = (byte)(922979425 ^ var39_2);
                var34_23 = var33_21.nextInt(var37_22);
                var35_24 = var34_23;
                var20_25 = var18_19 - var35_24;
                var22_26 = (int)var20_25;
                if (Factory.jarwsmbhjwbijwmk((int)var39_2) == 177657633) {
                    var39_2 = 73776604 ^ var39_2;
                    var23_27 = this;
                    var24_28 = var23_27.astralShipSelectionGui;
                    var24_28.click(var13_14, var22_26);
                    if (Factory.jarwsmbhjwbijwmk((int)var39_2) == 60175185) {
                        var39_2 = 1803453940 ^ var39_2;
                        return;
                    } else {
                        ** GOTO lbl-1000
                    }
                } else {
                    ** GOTO lbl-1000
                }
            }
        } else lbl-1000:
        // 5 sources

        {
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var39_2) != 60189520 || Factory.jarwsmbhjwbijwmk((int)var39_2) != (1376282206 ^ var39_2)) {
                    continue;
                }
                AstralMediator.QMHucVC4Eb = 1661352374 ^ var39_2;
                if (Factory.jarwsmbhjwbijwmk((int)var39_2) != 60175185 || Factory.jarwsmbhjwbijwmk((int)var39_2) != (1078448209 ^ var39_2)) continue;
                this.CQMg54U3ie = 2147190812 ^ var39_2;
                if (Factory.jarwsmbhjwbijwmk((int)var39_2) != 177657633 || Factory.jarwsmbhjwbijwmk((int)var39_2) != (868397510 ^ var39_2)) continue;
                this.CQMg54U3ie = 793994960 ^ var39_2;
                if (Factory.jarwsmbhjwbijwmk((int)var39_2) == 179275116 && Factory.jarwsmbhjwbijwmk((int)var39_2) == (1849406339 ^ var39_2)) break;
            }
            AstralMediator.QMHucVC4Eb = 2046872427 ^ var39_2;
            throw new IllegalAccessException();
        }
        while (Factory.jarwsmbhjwbijwmk((int)var39_2) != 182707266 || Factory.jarwsmbhjwbijwmk((int)var39_2) != (1933670037 ^ var39_2)) {
        }
        AstralMediator.QMHucVC4Eb = 1394664105 ^ var39_2;
        throw new RuntimeException("double");
    }

    private DivContainer getItemToSelect(int n, int n2) {
        int n3;
        block7: {
            block8: {
                block9: {
                    n3 = 0x72D8FFCA ^ (0x6F52518D ^ (this.CQMg54U3ie ^ n2));
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 125273814) break block7;
                    n3 = 0x42440FD6 ^ n3;
                    AstralMediator astralMediator = this;
                    DivContainer divContainer = astralMediator.getItemSelection(1559354951);
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 114548955) break block8;
                    n3 = 0x134830E3 ^ n3;
                    byte by = (byte)(0x45528003 ^ n3);
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 105880903) break block7;
                    n3 = 0x54832287 ^ n3;
                    double d = divContainer.getWidth(1109363703);
                    int n4 = (int)d;
                    byte by2 = (byte)(0x11D1A28F ^ n3);
                    int n5 = n4 / by2;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 42351422) break block9;
                    n3 = 0x21B468BC ^ n3;
                    int n6 = n5 * n;
                    int n7 = n6 + by;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 231016408) break block9;
                    n3 = 0x24DE84B2 ^ n3;
                    int n8 = n7 + n5;
                    int n9 = n8 - by;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 137034912) break block7;
                    n3 = 0x132736F6 ^ n3;
                    double d2 = divContainer.getInitPosX(404938149);
                    double d3 = n7;
                    double d4 = d2 + d3;
                    double d5 = divContainer.getInitPosY(638783209);
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 119715696) break block7;
                    n3 = 0x1A28C347 ^ n3;
                    double d6 = divContainer.getInitPosX(404938149);
                    double d7 = n9;
                    double d8 = d6 + d7;
                    double d9 = divContainer.getEndPosY(1548021488);
                    DivContainer divContainer2 = new DivContainer(d4, d5, d8, d9, 1703909899);
                    if (Factory.jarwsmbhjwbijwmk((int)n3) == 160315142) {
                        n3 = 0x69509A5F ^ n3;
                        return divContainer2;
                    }
                    break block8;
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 231016408 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x4980CDA4 ^ n3)) {
                        continue;
                    }
                    QMHucVC4Eb = 0x1899686C ^ n3;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 42351422 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x1CDECC92 ^ n3)) continue;
                    this.CQMg54U3ie = 0x7AA91F9F ^ n3;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) == 41579557 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x4B95CB1C ^ n3)) break;
                }
                this.CQMg54U3ie = 0x1A6EBD5F ^ n3;
                throw new IOException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n3) != 160315142 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x2877D1F4 ^ n3)) {
                    continue;
                }
                QMHucVC4Eb = 0x1610C4E1 ^ n3;
                if (Factory.jarwsmbhjwbijwmk((int)n3) == 114548955 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x639CDABE ^ n3)) break;
            }
            QMHucVC4Eb = 0x2AA701DE ^ n3;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 125273814 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x5E762CF3 ^ n3)) {
                continue;
            }
            this.CQMg54U3ie = 0x44FC45AA ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 137034912 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x1C145309 ^ n3)) continue;
            this.CQMg54U3ie = 0x692148B7 ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 119715696 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x5D5E2033 ^ n3)) continue;
            this.CQMg54U3ie = 0x5EBC532C ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) == 105880903 && Factory.jarwsmbhjwbijwmk((int)n3) == (0xFF9E004 ^ n3)) break;
        }
        this.CQMg54U3ie = 0x410D42A6 ^ n3;
        throw new RuntimeException();
    }

    private DivContainer getItemSelection(int n) {
        int n2;
        block6: {
            block9: {
                block7: {
                    block8: {
                        n2 = 0x701F3413 ^ (0x44F85F67 ^ (this.CQMg54U3ie ^ n));
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 166882627) break block6;
                        n2 = 0x1E1CB512 ^ n2;
                        byte by = (byte)(0x76266CE3 ^ n2);
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 82445386) break block7;
                        n2 = 0x4F59741E ^ n2;
                        byte by2 = (byte)(0x397F18D1 ^ n2);
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 258344702) break block6;
                        n2 = 0x2ED0D39C ^ n2;
                        AstralMediator astralMediator = this;
                        Gui gui = astralMediator.astralGui;
                        double d = gui.getWidth();
                        int n3 = (int)d;
                        byte by3 = (byte)(0x17AFCB1B ^ n2);
                        int n4 = by * by3;
                        int n5 = n3 - n4;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 233082415) break block8;
                        n2 = 0x9C28429 ^ n2;
                        n2 = 0x720A0E58 ^ n2;
                        int n6 = by + n5;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 34104030) break block9;
                        n2 = 0x49BE99BF ^ n2;
                        AstralMediator astralMediator2 = this;
                        Gui gui2 = astralMediator2.astralGui;
                        double d2 = gui2.getHeight();
                        int n7 = (int)d2;
                        int n8 = n7 - by2;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 156643833) {
                            n2 = 0x716EE8FD ^ n2;
                            double d3 = by;
                            double d4 = by2;
                            double d5 = n6;
                            double d6 = n8;
                            DivContainer divContainer = new DivContainer(d3, d4, d5, d6, 1703909899);
                            return divContainer;
                        }
                        break block6;
                    }
                    while (Factory.jarwsmbhjwbijwmk((int)n2) != 233082415 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x76943E9D ^ n2)) {
                    }
                    this.CQMg54U3ie = 0x6FD4C778 ^ n2;
                    throw new IllegalAccessException("double");
                }
                while (Factory.jarwsmbhjwbijwmk((int)n2) != 82445386 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x496A0654 ^ n2)) {
                }
                this.CQMg54U3ie = 0x367BF16D ^ n2;
                throw new IllegalAccessException("double");
            }
            while (Factory.jarwsmbhjwbijwmk((int)n2) != 34104030 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x4F7AD809 ^ n2)) {
            }
            QMHucVC4Eb = 0x1E3DE337 ^ n2;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 166882627 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x3ADEF6E8 ^ n2)) {
                continue;
            }
            QMHucVC4Eb = 0x8E836E9 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 258344702 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x4857BEFD ^ n2)) continue;
            this.CQMg54U3ie = 0x514399D7 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 156643833 && Factory.jarwsmbhjwbijwmk((int)n2) == (0xCA3D8C9 ^ n2)) break;
        }
        this.CQMg54U3ie = 0x2142E7EA ^ n2;
        throw new IOException();
    }

    private DivContainer getShipToSelect(int n, int n2) {
        int n3;
        block8: {
            block10: {
                block11: {
                    block9: {
                        n3 = 0x4E1D4F4 ^ (0x5C5BCDFF ^ (this.CQMg54U3ie ^ n2));
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 2680235) break block8;
                        n3 = 0x672F7FF5 ^ n3;
                        AstralMediator astralMediator = this;
                        DivContainer divContainer = astralMediator.getShipSelection(872908907);
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 133395825) break block9;
                        n3 = 0x904292 ^ n3;
                        byte by = (byte)(0x140EE4B8 ^ n3);
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 115134244) break block10;
                        n3 = 0x5C8FC25C ^ n3;
                        double d = divContainer.getWidth(1109363703);
                        int n4 = (int)d;
                        byte by2 = (byte)(0x488126E3 ^ n3);
                        int n5 = n4 / by2;
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 209351646) break block8;
                        n3 = 0x3D0254DC ^ n3;
                        int n6 = n5 * n;
                        int n7 = n6 + by;
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 61790417) break block11;
                        n3 = 0x416732D0 ^ n3;
                        int n8 = n7 + n5;
                        int n9 = n8 - by;
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 108661027) break block10;
                        n3 = 0x58F6164C ^ n3;
                        double d2 = divContainer.getInitPosX(404938149);
                        double d3 = n7;
                        double d4 = d2 + d3;
                        double d5 = divContainer.getInitPosY(638783209);
                        if (Factory.jarwsmbhjwbijwmk((int)n3) != 23325681) break block8;
                        n3 = 0xCF2954C ^ n3;
                        double d6 = divContainer.getInitPosX(404938149);
                        double d7 = n9;
                        double d8 = d6 + d7;
                        double d9 = divContainer.getEndPosY(1548021488);
                        DivContainer divContainer2 = new DivContainer(d4, d5, d8, d9, 1703909899);
                        if (Factory.jarwsmbhjwbijwmk((int)n3) == 196287351) {
                            n3 = 0x41DB088B ^ n3;
                            return divContainer2;
                        }
                        break block11;
                    }
                    while (Factory.jarwsmbhjwbijwmk((int)n3) != 133395825 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x1360E16C ^ n3)) {
                    }
                    this.CQMg54U3ie = 0xC169D80 ^ n3;
                    throw new IOException("double");
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)n3) != 196287351 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x6657B04A ^ n3)) {
                        continue;
                    }
                    QMHucVC4Eb = 0x9E729AA ^ n3;
                    if (Factory.jarwsmbhjwbijwmk((int)n3) == 61790417 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x1D2E38E6 ^ n3)) break;
                }
                QMHucVC4Eb = 0x2F8260EE ^ n3;
                throw new IllegalAccessException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n3) != 115134244 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x7E89CA65 ^ n3)) {
                    continue;
                }
                QMHucVC4Eb = 0x2CDCF8E8 ^ n3;
                if (Factory.jarwsmbhjwbijwmk((int)n3) == 108661027 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x52CC414C ^ n3)) break;
            }
            this.CQMg54U3ie = 0x7F609B6A ^ n3;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 2680235 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x29F3EA32 ^ n3)) {
                continue;
            }
            this.CQMg54U3ie = 0x719BBCD9 ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 6552369 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x132CB207 ^ n3)) continue;
            this.CQMg54U3ie = 0x741F1617 ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) != 23325681 || Factory.jarwsmbhjwbijwmk((int)n3) != (0x25ABCB91 ^ n3)) continue;
            QMHucVC4Eb = 0x5EC310A2 ^ n3;
            if (Factory.jarwsmbhjwbijwmk((int)n3) == 209351646 && Factory.jarwsmbhjwbijwmk((int)n3) == (0x7E4FC088 ^ n3)) break;
        }
        this.CQMg54U3ie = 0x524588FB ^ n3;
        throw new IllegalAccessException();
    }

    /*
     * Unable to fully structure code
     */
    private DivContainer getShipSelection(int var1_1) {
        block8: {
            var21_2 = 391099138 ^ (638182629 ^ (this.CQMg54U3ie ^ var1_1));
            if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 85384559) break block8;
            var21_2 = 1591191666 ^ var21_2;
            var2_3 = (short)(1538923477 ^ var21_2);
            if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 28888937) ** GOTO lbl-1000
            var21_2 = 1700995281 ^ var21_2;
            var4_4 = (byte)(1054424019 ^ var21_2);
            if (Factory.jarwsmbhjwbijwmk((int)var21_2) == 163896745) {
                var21_2 = 1552278548 ^ var21_2;
                var5_5 = this;
                var6_6 = var5_5.astralShipSelectionGui;
                var7_7 = var6_6.getWidth();
                var13_8 = var2_3;
                var9_9 = var7_7 - var13_8;
                if (Factory.jarwsmbhjwbijwmk((int)var21_2) == 244552533) {
                    var21_2 = 761452036 ^ var21_2;
                    var3_10 = (byte)(1329465823 ^ var21_2);
                    var11_11 = var4_4 + var3_10;
                    if (Factory.jarwsmbhjwbijwmk((int)var21_2) == 159939919) {
                        var21_2 = 1284553368 ^ var21_2;
                        var15_13 = var2_3;
                        var17_14 = var4_4;
                        var19_15 = var11_11;
                        var12_12 = new DivContainer(var15_13, var17_14, var9_9, var19_15, 1703909899);
                        return var12_12;
                    } else {
                        ** GOTO lbl-1000
                    }
                }
            } else lbl-1000:
            // 4 sources

            {
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 57918533 || Factory.jarwsmbhjwbijwmk((int)var21_2) != (1765711697 ^ var21_2)) {
                        continue;
                    }
                    this.CQMg54U3ie = 1017479975 ^ var21_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 28888937 || Factory.jarwsmbhjwbijwmk((int)var21_2) != (136506398 ^ var21_2)) continue;
                    AstralMediator.QMHucVC4Eb = 880954651 ^ var21_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 159939919 || Factory.jarwsmbhjwbijwmk((int)var21_2) != (1729150806 ^ var21_2)) continue;
                    this.CQMg54U3ie = 2120491017 ^ var21_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var21_2) == 163896745 && Factory.jarwsmbhjwbijwmk((int)var21_2) == (798050230 ^ var21_2)) break;
                }
                AstralMediator.QMHucVC4Eb = 650992777 ^ var21_2;
                throw new IOException();
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var21_2) != 244552533 || Factory.jarwsmbhjwbijwmk((int)var21_2) != (1615768244 ^ var21_2)) {
                continue;
            }
            AstralMediator.QMHucVC4Eb = 1552640182 ^ var21_2;
            if (Factory.jarwsmbhjwbijwmk((int)var21_2) == 85384559 && Factory.jarwsmbhjwbijwmk((int)var21_2) == (1622723037 ^ var21_2)) break;
        }
        AstralMediator.QMHucVC4Eb = 1032699936 ^ var21_2;
        throw new IllegalAccessException("double");
    }

    static {
        AstralMediator.nothing_to_see_here[0] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u20ac\u00e2\u00a1\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[1] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00b6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u201e\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[2] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u00bf\u00e2\u00a0\u0178\u00e2\u00a0\u203a\u00e2\u00a0\u00bb\u00e2\u00a3\u00bf\u00e2\u00a0\u2020\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[3] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2020\u00e2\u00a3\u20ac\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a0\u201a\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[4] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a0\u00bb\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u2026\u00e2\u00a0\u203a\u00e2\u00a0\u2039\u00e2\u00a0\u02c6\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[5] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u02dc\u00e2\u00a2\u00bc\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u0192\u00e2\u00a0\u00a0\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[6] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u0178\u00e2\u00a1\u00bf\u00e2\u00a0\u0192\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[7] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u203a\u00e2\u00a3\u203a\u00e2\u00a3\u00ab\u00e2\u00a1\u201e\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00a6\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[8] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00a0\u00e2\u00a3\u00b4\u00e2\u00a3\u00be\u00e2\u00a1\u2020\u00e2\u00a0\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u00b7\u00e2\u00a0\u201a\u00e2\u00a0\u00a8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b6\u00e2\u00a3\u00a6\u00e2\u00a3\u00a4\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[9] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00a4\u00e2\u00a3\u00be\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a1\u00bf\u00e2\u00a0\u2039\u00e2\u00a0\ufffd\u00e2\u00a2\u20ac\u00e2\u00a1\u00b6\u00e2\u00a0\u00aa\u00e2\u00a3\u2030\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[10] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\ufffd\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00a6\u00e2\u00a1\u2122\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\ufffd\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[11] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u02c6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2021\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00a6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[12] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00a0\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[13] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u201e\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[14] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[15] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00a0\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u00bf\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[16] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u0192\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralMediator.nothing_to_see_here[17] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b9\u00e2\u00a3\u00bf\u00e2\u00a3\u00b5\u00e2\u00a3\u00be\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00af\u00e2\u00a1\ufffd\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        int n = 0x4189B427 ^ (0x7515FC03 ^ ofus.get((String)"exaitretxdrktjdc"));
        int n2 = new Random(-5537756562146126415L).nextInt();
        QMHucVC4Eb = 0xFEBEF009 ^ n2;
    }

    private static String thisIsAInsaneEncryptionMethod(byte[] byArray, int n) {
        int n2;
        int n3;
        int n4;
        byte by;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        int n12;
        int n13;
        int n14;
        int n15;
        int n16;
        int n17;
        int n18;
        int n19;
        int n20;
        int n21;
        int n22;
        int n23;
        int n24;
        int n25;
        int n26;
        int n27;
        int n28;
        int n29;
        int n30;
        byte by2;
        int n31;
        int n32;
        int n33;
        int n34;
        int n35;
        int n36;
        int n37;
        int n38;
        int n39;
        int n40;
        int n41;
        int n42;
        int n43;
        int n44;
        int n45;
        int n46;
        int n47;
        int n48;
        int n49;
        int n50;
        int n51;
        int n52;
        int n53;
        int n54;
        int n55;
        int n56;
        int n57;
        int n58;
        int n59;
        int n60;
        int n61;
        int n62;
        int n63;
        int n64;
        int n65;
        int n66;
        int n67;
        int n68;
        int n69;
        int n70;
        int n71;
        int n72;
        int n73;
        int n74;
        int n75;
        int n76;
        int n77;
        int n78;
        int n79;
        int n80;
        int n81;
        int n82;
        int n83;
        int n84;
        int n85;
        int n86;
        int n87;
        int n88;
        int n89;
        int n90;
        int n91;
        int n92;
        int n93;
        String string = Integer.toString(n);
        byte[] byArray2 = string.getBytes();
        int n94 = 93;
        byte[] byArray3 = new byte[n94];
        int n95 = 0;
        byArray3[n95] = n93 = 25;
        int n96 = 1;
        byArray3[n96] = n92 = 95;
        int n97 = 2;
        byArray3[n97] = n91 = 40;
        int n98 = 3;
        byArray3[n98] = n90 = 19;
        int n99 = 4;
        byArray3[n99] = n89 = 72;
        int n100 = 5;
        byArray3[n100] = n88 = 12;
        int n101 = 6;
        byArray3[n101] = n87 = 52;
        int n102 = 7;
        byArray3[n102] = n86 = 11;
        int n103 = 8;
        byArray3[n103] = n85 = 108;
        int n104 = 9;
        byArray3[n104] = n84 = 98;
        int n105 = 10;
        byArray3[n105] = n83 = 60;
        int n106 = 11;
        byArray3[n106] = n82 = 51;
        int n107 = 12;
        byArray3[n107] = n81 = 125;
        int n108 = 13;
        byArray3[n108] = n80 = 6;
        int n109 = 14;
        byArray3[n109] = n79 = 100;
        int n110 = 15;
        byArray3[n110] = n78 = 57;
        int n111 = 16;
        byArray3[n111] = n77 = 105;
        int n112 = 17;
        byArray3[n112] = n76 = 42;
        int n113 = 18;
        byArray3[n113] = n75 = 84;
        int n114 = 19;
        byArray3[n114] = n74 = 58;
        int n115 = 20;
        byArray3[n115] = n73 = 75;
        int n116 = 21;
        byArray3[n116] = n72 = 33;
        int n117 = 22;
        byArray3[n117] = n71 = 49;
        int n118 = 23;
        byArray3[n118] = n70 = 125;
        int n119 = 24;
        byArray3[n119] = n69 = 69;
        int n120 = 25;
        byArray3[n120] = n68 = 77;
        int n121 = 26;
        byArray3[n121] = n67 = 6;
        int n122 = 27;
        byArray3[n122] = n66 = 48;
        int n123 = 28;
        byArray3[n123] = n65 = 17;
        int n124 = 29;
        byArray3[n124] = n64 = 78;
        int n125 = 30;
        byArray3[n125] = n63 = 65;
        int n126 = 31;
        byArray3[n126] = n62 = 4;
        int n127 = 32;
        byArray3[n127] = n61 = 86;
        int n128 = 33;
        byArray3[n128] = n60 = 47;
        int n129 = 34;
        byArray3[n129] = n59 = 4;
        int n130 = 35;
        byArray3[n130] = n58 = 125;
        int n131 = 36;
        byArray3[n131] = n57 = 23;
        int n132 = 37;
        byArray3[n132] = n56 = 124;
        int n133 = 38;
        byArray3[n133] = n55 = 27;
        int n134 = 39;
        byArray3[n134] = n54 = 44;
        int n135 = 40;
        byArray3[n135] = n53 = 93;
        int n136 = 41;
        byArray3[n136] = n52 = 111;
        int n137 = 42;
        byArray3[n137] = n51 = 26;
        int n138 = 43;
        byArray3[n138] = n50 = 64;
        int n139 = 44;
        byArray3[n139] = n49 = 41;
        int n140 = 45;
        byArray3[n140] = n48 = 21;
        int n141 = 46;
        byArray3[n141] = n47 = 65;
        int n142 = 47;
        byArray3[n142] = n46 = 101;
        int n143 = 48;
        byArray3[n143] = n45 = 74;
        int n144 = 49;
        byArray3[n144] = n44 = 24;
        int n145 = 50;
        byArray3[n145] = n43 = 38;
        int n146 = 51;
        byArray3[n146] = n42 = 116;
        int n147 = 52;
        byArray3[n147] = n41 = 32;
        int n148 = 53;
        byArray3[n148] = n40 = 114;
        int n149 = 54;
        byArray3[n149] = n39 = 75;
        int n150 = 55;
        byArray3[n150] = n38 = 96;
        int n151 = 56;
        byArray3[n151] = n37 = 29;
        int n152 = 57;
        byArray3[n152] = n36 = 107;
        int n153 = 58;
        byArray3[n153] = n35 = 113;
        int n154 = 59;
        byArray3[n154] = n34 = 69;
        int n155 = 60;
        byArray3[n155] = n33 = 95;
        int n156 = 61;
        byArray3[n156] = n32 = 97;
        int n157 = 62;
        byArray3[n157] = n31 = 63;
        int n158 = 63;
        byArray3[n158] = by2 = 1;
        int n159 = 64;
        byArray3[n159] = n30 = 100;
        int n160 = 65;
        byArray3[n160] = n29 = 79;
        int n161 = 66;
        byArray3[n161] = n28 = 78;
        int n162 = 67;
        byArray3[n162] = n27 = 76;
        int n163 = 68;
        byArray3[n163] = n26 = 24;
        int n164 = 69;
        byArray3[n164] = n25 = 77;
        int n165 = 70;
        byArray3[n165] = n24 = 36;
        int n166 = 71;
        byArray3[n166] = n23 = 88;
        int n167 = 72;
        byArray3[n167] = n22 = 69;
        int n168 = 73;
        byArray3[n168] = n21 = 67;
        int n169 = 74;
        byArray3[n169] = n20 = 81;
        int n170 = 75;
        byArray3[n170] = n19 = 58;
        int n171 = 76;
        byArray3[n171] = n18 = 104;
        int n172 = 77;
        byArray3[n172] = n17 = 48;
        int n173 = 78;
        byArray3[n173] = n16 = 97;
        int n174 = 79;
        byArray3[n174] = n15 = 112;
        int n175 = 80;
        byArray3[n175] = n14 = 124;
        int n176 = 81;
        byArray3[n176] = n13 = 64;
        int n177 = 82;
        byArray3[n177] = n12 = 54;
        int n178 = 83;
        byArray3[n178] = n11 = 35;
        int n179 = 84;
        byArray3[n179] = n10 = 46;
        int n180 = 85;
        byArray3[n180] = n9 = 21;
        int n181 = 86;
        byArray3[n181] = n8 = 92;
        int n182 = 87;
        byArray3[n182] = n7 = 69;
        int n183 = 88;
        byArray3[n183] = n6 = 11;
        int n184 = 89;
        byArray3[n184] = n5 = 97;
        int n185 = 90;
        byArray3[n185] = by = 1;
        int n186 = 91;
        byArray3[n186] = n4 = 81;
        int n187 = 92;
        byArray3[n187] = n3 = 43;
        for (int i = 0; i < (n2 = byArray.length); ++i) {
            byte by3;
            byte by4;
            byte by5 = byArray[i];
            int n188 = byArray2.length;
            int n189 = i % n188;
            byte by6 = byArray2[n189];
            int n190 = by5 ^ by6;
            byArray[i] = by4 = (byte)n190;
            byte by7 = byArray[i];
            int n191 = byArray3.length;
            int n192 = i % n191;
            byte by8 = byArray3[n192];
            int n193 = by7 ^ by8;
            byArray[i] = by3 = (byte)n193;
        }
        Charset charset = StandardCharsets.UTF_16;
        String string2 = new String(byArray, charset);
        return string2;
    }

    private static int rwurrwdaddxgwsuy(int n, int n2) {
        return n2 ^ n;
    }
}
