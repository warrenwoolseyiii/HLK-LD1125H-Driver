CC=gcc
ARMCC=aarch64-linux-gnu-gcc
CFLAGS=-I.

OBJDIR=build
SRCDIR=src

VERSION=$(shell grep -oP '(?<=VERSION_MAJOR ).*' src/version.h).$(shell grep -oP '(?<=VERSION_MINOR ).*' src/version.h).$(shell grep -oP '(?<=VERSION_REV ).*' src/version.h)


.PHONY: all clean

all: $(OBJDIR)/hlk_ld1125h-amd64-$(VERSION) $(OBJDIR)/hlk_ld1125h-arm64-$(VERSION)

$(OBJDIR)/hlk_ld1125h-amd64-$(VERSION): $(OBJDIR)/hlk_ld1125h.o
	$(CC) -o $@ $<

$(OBJDIR)/hlk_ld1125h-arm64-$(VERSION): $(OBJDIR)/hlk_ld1125h_arm.o
	$(ARMCC) -o $@ $<

$(OBJDIR)/%.o: $(SRCDIR)/%.c | $(OBJDIR)
	$(CC) -c -o $@ $< $(CFLAGS)

$(OBJDIR)/%_arm.o: $(SRCDIR)/%.c | $(OBJDIR)
	$(ARMCC) -c -o $@ $< $(CFLAGS)

$(OBJDIR):
	mkdir $(OBJDIR)

clean:
	rm -f $(OBJDIR)/*

.PHONY: install

install: all
	@echo "Installing..."
	@if [ -f /usr/local/bin/hlk_ld1125h-amd64-* ]; then \
		echo "Removing existing version..."; \
		rm /usr/local/bin/hlk_ld1125h-amd64-*; \
	fi
	@if [ -f /usr/local/bin/hlk_ld1125h-arm64-* ]; then \
		echo "Removing existing version..."; \
		rm /usr/local/bin/hlk_ld1125h-arm64-*; \
	fi
	cp $(OBJDIR)/hlk_ld1125h-amd64-$(VERSION) /usr/local/bin/
	cp $(OBJDIR)/hlk_ld1125h-arm64-$(VERSION) /usr/local/bin/
	@echo "Installation done!"

