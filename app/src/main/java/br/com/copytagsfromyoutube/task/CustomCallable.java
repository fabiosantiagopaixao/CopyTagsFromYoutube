package br.com.copytagsfromyoutube.task;

public interface CustomCallable<R> {
    public void setUiForLoading();
    public void setDataAfterLoading(R result);
    public R call() throws Exception;
}
