package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.measure.MeasureProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence.
 */
public abstract class MutableSequenceRuntime<S extends Sequence, A> implements SequenceRuntime<A>  {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRuntime.class);

  private MeasureProvider measureProvider;
  private S sequence;
  private Event<A> nextEvent;
  private long length;
  private MutableTock tock;
  private Tock sealedTock;

  @Override
  public Tick getTick() {
    return sequence.getTick();
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  /**
   * Set measure provider.
   *
   * @param measureProvider measure provider
   */
  public void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    if (sequence != null) {
      reload();
    }
  }

  /**
   * Set configuration for this sequence runtime.
   *
   * @param sequence sequence configuration
   */
  public void setConfiguration(S sequence) {
    this.sequence = sequence;
    if (measureProvider != null) {
      reload();
    }
  }

  public S getConfiguration() {
    return sequence;
  }

  /**
   * Reload the sequence runtime.
   */
  private void reload() {
    LOG.debug("Reloading runtime sequence");
    TickConverter converter = new SameTimeUnitTickConverter(DefaultTick.BEAT,
        getConfiguration().getTick());
    this.length = converter.convert(getConfiguration().getLength());
    // FIX : Currently reload resets tock to start of sequencer - we should set it to now
    tock = new MutableTock(getConfiguration().getTick(), 0);
    sealedTock = new SealedTock(tock);
  }

  protected abstract Event<A> getNextEvent(Tock tock);

  protected long getLength() {
    return length;
  }

  private boolean isActive() {
    return tock.getCount() < getLength();
  }

  private Event<A> getNextEventInternal(MutableTock tock) {
    Event<A> event = getNextEvent(sealedTock);
    /*
     * Now increment internal tock to the time of the provided event
     */
    tock.setCount(event.getTime());
    /*
     * If the response was a scan forward signal then we return a null event since no event
     * was found and we've handled the scanning forward above.
     */
    if (event instanceof ScanForwardEvent) {
      return null;
    }
    return event;
  }

  @Override
  public Event<A> peek() {
    if (nextEvent == null) {
      nextEvent = getNextEventInternal(tock);
    }
    return nextEvent;
  }

  @Override
  public Event<A> pop() {
    Event<A> thisEvent = nextEvent;
    if (length < 0 || isActive()) {
      nextEvent = getNextEventInternal(tock);
    } else {
      nextEvent = null;
    }
    return thisEvent;
  }
}
