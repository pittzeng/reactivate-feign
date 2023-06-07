package com.yigou.common.feign.bean;

import com.yigou.common.feign.exception.RetryException;

/**
 * 超时重试
 */
public interface TimeOutRetry extends Cloneable{
    TimeOutRetry NEVER_RETRY=new TimeOutRetry() {
        public void continueOrPropagate(RetryException e) {
            throw e;
        }
        public TimeOutRetry clone(){return  this;}
    };
    void continueOrPropagate(RetryException var1);

    TimeOutRetry clone();

    public static class DefaultTimeOutRetry implements TimeOutRetry{
        /**
         * 是否重试
         */
        private boolean retry;

        /**
         * 最大重试次数
         */
        private int maxAttempts;
        /**
         * 当前次数
         */

        private int attempt;
        /**
         * 最小时间
         */
        private long period;
        /**
         * 最大时间
         */
        private long maxPeriod;
        long sleptForMillis;
        public DefaultTimeOutRetry(){
            this.retry=false;
        }
        public DefaultTimeOutRetry(long period,long maxPeriod,int maxAttempts){
            this.retry=true;
            this.period=period;
            this.maxPeriod=maxPeriod;
            this.maxAttempts=maxAttempts;
            this.attempt=1;
        }

        @Override
        public void continueOrPropagate(RetryException e) {
            if (this.attempt++ >= this.maxAttempts) {
                throw e;
            } else {
                long interval;
                if (e.getRetryDate() != null) {
                    interval = e.getRetryDate().getTime() - System.currentTimeMillis();
                    if (interval > this.maxPeriod) {
                        interval = this.maxPeriod;
                    }

                    if (interval < 0L) {
                        return;
                    }
                }else{
                    interval = this.nextMaxInterval();
                }


                try {
                    Thread.sleep(interval);
                } catch (InterruptedException var5) {
                    Thread.currentThread().interrupt();
                    throw e;
                }

                this.sleptForMillis += interval;
            }
        }
        long nextMaxInterval() {
            long interval = (long)((double)this.period * Math.pow(1.5, (double)(this.attempt - 1)));
            return interval > this.maxPeriod ? this.maxPeriod : interval;
        }

        @Override
        public TimeOutRetry clone() {
            return new DefaultTimeOutRetry(this.period, this.maxPeriod, this.maxAttempts);
        }


        public boolean isRetry() {
            return retry;
        }

        public void setRetry(boolean retry) {
            this.retry = retry;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getAttempt() {
            return attempt;
        }

        public void setAttempt(int attempt) {
            this.attempt = attempt;
        }

        public long getPeriod() {
            return period;
        }

        public void setPeriod(long period) {
            this.period = period;
        }

        public long getMaxPeriod() {
            return maxPeriod;
        }

        public void setMaxPeriod(long maxPeriod) {
            this.maxPeriod = maxPeriod;
        }
    }

}
