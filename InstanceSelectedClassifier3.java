/*
 *    DriftDetectionMethodClassifier.java
 *    Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *    @author Manuel Baena (mbaena@lcc.uma.es)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package moa.classifiers.drift;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.LinkedList;
import java.util.List;

import moa.capabilities.CapabilitiesHandler;
import moa.capabilities.Capability;
import moa.capabilities.ImmutableCapabilities;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.MultiClassClassifier;
import moa.core.Measurement;
import moa.core.Utils;
import moa.options.ClassOption;

/**
 * Class for handling concept drift datasets with a wrapper on a
 * classifier.<p>
 *
 * Valid options are:<p>
 *
 * -l classname <br>
 * Specify the full class name of a classifier as the basis for
 * the concept drift classifier.<p>
 * -d Drift detection method to use<br>
 *
 * @author Manuel Baena (mbaena@lcc.uma.es)
 * @version 1.1
 */
public class InstanceSelectedClassifier3 extends AbstractClassifier implements MultiClassClassifier,
                                                                                  CapabilitiesHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public String getPurposeString() {
        return "Classifier that replaces the current classifier with a new one when a change is detected in accuracy.";
    }
    
    public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
            "Classifier to train.", Classifier.class, "bayes.NaiveBayes");
   

    protected Classifier classifier;

    protected Classifier newclassifier;



    protected boolean newClassifierReset;
    //protected int numberInstances = 0;

    
    @Override
    public void resetLearningImpl() {
        this.classifier = ((Classifier) getPreparedClassOption(this.baseLearnerOption)).copy();
        this.newclassifier = this.classifier.copy();
        this.classifier.resetLearning();
        this.newclassifier.resetLearning();
        this.newClassifierReset = false;
    }

    protected int changeDetected = 0;

    protected int warningDetected = 0;

    @Override
    public void trainOnInstanceImpl(Instance inst) {
        // Obtém os votos do classificador para a instância
        double[] votes = this.classifier.getVotesForInstance(inst);

        if (votes != null && votes.length > 0) {
            // Calcula o índice da classe predita (aquela com maior voto)
            int predictedClass = Utils.maxIndex(votes);
            double confidence = votes[predictedClass] / Utils.sum(votes); // Confiança da predição

            int trueClass = (int) inst.classValue();

            // Treina a instância somente se a confiança for menor que 75%
            if (confidence < 0.75 || predictedClass != trueClass) {
                this.classifier.trainOnInstance(inst);
            }
        } else {
            // Caso não existam votos (não foi possível predizer), treine por segurança
            this.classifier.trainOnInstance(inst);
        }
    }


    public double[] getVotesForInstance(Instance inst) {
        return this.classifier.getVotesForInstance(inst);
    }

    @Override
    public boolean isRandomizable() {
        return true;
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {
        ((AbstractClassifier) this.classifier).getModelDescription(out, indent);
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        List<Measurement> measurementList = new LinkedList<Measurement>();
        Measurement[] modelMeasurements = ((AbstractClassifier) this.classifier).getModelMeasurements();
        if (modelMeasurements != null) {
            for (Measurement measurement : modelMeasurements) {
                measurementList.add(measurement);
            }
        }
        this.changeDetected = 0;
        this.warningDetected = 0;
        return measurementList.toArray(new Measurement[measurementList.size()]);
    }

    @Override
    public ImmutableCapabilities defineImmutableCapabilities() {
        if (this.getClass() == InstanceSelectedClassifier3.class)
            return new ImmutableCapabilities(Capability.VIEW_STANDARD, Capability.VIEW_LITE);
        else
            return new ImmutableCapabilities(Capability.VIEW_STANDARD);
    }
}
