CC=gcc
CFLAGS=-I.

OBJDIR=build
SRCDIR=src

$(OBJDIR)/hlk_ld1125h: $(OBJDIR)/hlk_ld1125h.o
	$(CC) -o $@ $<

$(OBJDIR)/%.o: $(SRCDIR)/%.c | $(OBJDIR)
	$(CC) -c -o $@ $<

$(OBJDIR):
	mkdir $(OBJDIR)

.PHONY: clean

clean:
	rm -f $(OBJDIR)/*.o $(OBJDIR)/hlk_ld1125h
